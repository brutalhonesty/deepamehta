package de.deepamehta.core;

import de.deepamehta.core.model.IndexMode;

import org.codehaus.jettison.json.JSONObject;
import java.util.Map;
import java.util.Set;



/**
 * Specification of a topic type -- part of DeepaMehta's type system, like a class.
 *
 * @author <a href="mailto:jri@deepamehta.de">Jörg Richter</a>
 */
public interface TopicType extends Type {

    String getDataTypeUri();

    void setDataTypeUri(String dataTypeUri);

    // ---

    Set<IndexMode> getIndexModes();

    void setIndexModes(Set<IndexMode> indexModes);

    // ---

    Map<String, AssociationDefinition> getAssocDefs();

    AssociationDefinition getAssocDef(String assocDefUri);

    // void addAssocDef(AssociationDefinition assocDef);    // FIXME: not in use
}