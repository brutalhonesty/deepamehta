package de.deepamehta.core.service;

import de.deepamehta.core.model.Association;
import de.deepamehta.core.model.AssociationData;
import de.deepamehta.core.model.AssociationTypeData;
import de.deepamehta.core.model.ClientContext;
import de.deepamehta.core.model.CommandParams;
import de.deepamehta.core.model.CommandResult;
import de.deepamehta.core.model.PluginInfo;
import de.deepamehta.core.model.TopicValue;
import de.deepamehta.core.model.Topic;
import de.deepamehta.core.model.TopicData;
import de.deepamehta.core.model.TopicType;
import de.deepamehta.core.model.TopicTypeData;
import de.deepamehta.core.model.RelatedTopic;
import de.deepamehta.core.storage.DeepaMehtaTransaction;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;



/**
 * Specification of the DeepaMehta core service -- the heart of DeepaMehta.
 * <p>
 * The responsibility of the DeepaMehta core service is to orchestrate the control flow and allow plugins to hook in.
 * The main duties of the DeepaMehta core service are to provide access to the storage layer and to trigger hooks of
 * the registered plugins.
 * <p>
 * The DeepaMehta core service is a realization of the <i>Inversion of Control</i> pattern.
 * <p>
 * The DeepaMehta core service provides methods to deal with topics, relations, types, commands, and plugins.
 * <p>
 * Plugin developer notes: Inside the {@link Plugin} and {@link Migration} classes an instance of the DeepaMehta
 * core service is available through the <code>dms</code> object.
 */
public interface CoreService {

    // === Topics ===

    public Topic getTopic(long id, ClientContext clientContext);

    /**
     * Looks up a single topic by exact property value.
     * If no such topic exists <code>null</code> is returned.
     * If more than one topic is found a runtime exception is thrown.
     * <p>
     * IMPORTANT: Looking up a topic this way requires the property to be indexed with indexing mode <code>KEY</code>.
     * This is achieved by declaring the respective data field with <code>indexing_mode: "KEY"</code>
     * (for statically declared data field, typically in <code>types.json</code>) or
     * by calling DataField's {@link DataField#setIndexingMode} method with <code>"KEY"</code> as argument
     * (for dynamically created data fields, typically in migration classes).
     */
    public Topic getTopic(String key, TopicValue value);

    // public Topic getTopic(String typeUri, String key, TopicValue value);

    /**
     * Returns a property value of a topic.
     * If the topic has no such property a "no-value" representing {@link TopicValue} object is returned.
     */
    // public TopicValue getTopicProperty(long topicId);

    // public List<Topic> getTopics(String typeUri);

    /**
     * Looks up topics by exact property value.
     * If no such topics exists an empty list is returned.
     * <p>
     * IMPORTANT: Looking up a topic this way requires the property to be indexed with indexing mode <code>KEY</code>.
     * This is achieved by declaring the respective data field with <code>indexing_mode: "KEY"</code>
     * (for statically declared data field, typically in <code>types.json</code>) or
     * by calling DataField's {@link DataField#setIndexingMode} method with <code>"KEY"</code> as argument
     * (for dynamically created data fields, typically in migration classes).
     */
    // public List<Topic> getTopics(String key, Object value);

    /**
     * Retrieves topics and relationships that are directly connected to the given topic, optionally filtered
     * by topic types and relation types.
     *
     * IMPORTANT: the topics and relations returned by this method provide no properties.
     * To initialize the properties needed by your plugin define its providePropertiesHook().
     *
     * @param   includeTopicTypes   The include topic type filter (optional).
     *                              A list of topic type URIs (strings), e.g. "de/deepamehta/core/topictype/Note".
     *                              Null or an empty list switches the filter off.
     * @param   includeRelTypes     The include relation type filter (optional).
     *                              A list of strings of the form "<relTypeName>[;<direction>]",
     *                              e.g. "TOPICMAP_TOPIC;INCOMING".
     *                              Null or an empty list switches the filter off.
     * @param   excludeRelTypes     The exclude relation type filter (optional).
     *                              A list of strings of the form "<relTypeName>[;<direction>]",
     *                              e.g. "SEARCH_RESULT;OUTGOING".
     *                              Null or an empty list switches the filter off.
     *
     * @return  The related topics, each one as a pair: the topic (a Topic object), and the connecting relation
     *          (a Relation object).
     */
    // public List<RelatedTopic> getRelatedTopics(long topicId, List<String> includeTopicTypes,
    //                                                          List<String> includeRelTypes,
    //                                                          List<String> excludeRelTypes);

    /**
     * Performs a fulltext search.
     *
     * @param   fieldUri    The URI of the data field to search. If null is provided all fields are searched.
     * @param   wholeWord   If true the searchTerm is regarded as whole word.
     *                      If false the searchTerm is regarded as begin-of-word substring.
     */
    public Set<Topic> searchTopics(String searchTerm, String fieldUri, boolean wholeWord,
                                                                       ClientContext clientContext);

    public Topic createTopic(TopicData topicData, ClientContext clientContext);

    public Topic updateTopic(TopicData topicData, ClientContext clientContext);

    public void deleteTopic(long topicId, ClientContext clientContext);

    // === Associations ===

    // public Relation getRelation(long id);

    /**
     * Returns the relation between two topics. If no such relation exists null is returned.
     * If more than one relation exists, an exception is thrown.
     *
     * @param   typeId      Relation type filter. Pass <code>null</code> to switch filter off.
     * @param   isDirected  Direction filter. Pass <code>true</code> if direction matters. In this case the relation
     *                      is expected to be directed <i>from</i> source topic <i>to</i> destination topic.
     */
    // public Relation getRelation(long srcTopicId, long dstTopicId, String typeId, boolean isDirected);

    /**
     * Returns the relations between two topics. If no such relation exists an empty list is returned.
     *
     * @param   typeId      Relation type filter. Pass <code>null</code> to switch filter off.
     * @param   isDirected  Direction filter. Pass <code>true</code> if direction matters. In this case the relation
     *                      is expected to be directed <i>from</i> source topic <i>to</i> destination topic.
     */
    public Set<Association> getAssociations(long topic1Id, long topic2Id, String assocTypeUri);

    public Association createAssociation(AssociationData assoc, ClientContext clientContext);

    // public void setRelationProperties(long id, Properties properties);

    public void deleteAssociation(long assocId, ClientContext clientContext);

    // === Types ===

    public Set<String> getTopicTypeUris();

    public TopicType getTopicType(String topicTypeUri, ClientContext clientContext);

    public TopicType createTopicType(TopicTypeData topicTypeData, ClientContext clientContext);

    public Topic createAssociationType(AssociationTypeData assocTypeData, ClientContext clientContext);

    // public void addDataField(String typeUri, DataField dataField);

    // public void updateDataField(String typeUri, DataField dataField);

    // public void setDataFieldOrder(String typeUri, List<String> fieldUris);

    // public void removeDataField(String typeUri, String fieldUri);

    // === Commands ===

    public CommandResult executeCommand(String command, CommandParams params, ClientContext clientContext);

    // === Plugins ===

    public void registerPlugin(Plugin plugin);

    public void unregisterPlugin(String pluginId);

    public Plugin getPlugin(String pluginId);

    public Set<PluginInfo> getPluginInfo();

    public void runPluginMigration(Plugin plugin, int migrationNr, boolean isCleanInstall);

    // === Misc ===

    public DeepaMehtaTransaction beginTx();

    /**
     * Triggers the ALL_PLUGINS_READY hook.
     * Called once all bundles are activated.
     * <p>
     * Called from the OSGi framework.
     * Not meant to be called by a plugin developer.
     */
    public void pluginsReady();

    /**
     * Setups the database to be compatible with this core service.
     * <p>
     * Called from the core activator.
     * Not meant to be called by a plugin developer.
     */
    public void setupDB();

    /**
     * Shuts down the database.
     * Called when the core service stops.
     * <p>
     * Called from the core activator.
     * Not meant to be called by a plugin developer.
     */
    public void shutdown();
}
