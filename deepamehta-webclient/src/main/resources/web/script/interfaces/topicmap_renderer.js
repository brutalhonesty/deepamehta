/**
 * An abstraction of the view component that occupies the left part of the DeepaMehta window (the "canvas").
 * The abstraction comprises:
 *     - A model of the topics and association that are currently displayed.
 *     - A model for the current selection.
 *     - A model for the current translation.
 *     - The view element (the "dom" property).
 *
 * The Webclient and the Topicmaps modules, as well as the SplitPanel is coded to this interface.
 *
 * Note: the concept of a (persistent) topicmap does not appear here.
 * It is only introduced by the Topicmaps module.
 */
function TopicmapRenderer() {

    this.get_info = function() {}

    // ---

    /**
     * @param   topic       an object with "id", "type_uri", "value" properties and optional "x", "y" properties.
     * @param   do_select   Optional: if true, the topic is selected.
     */
    this.add_topic = function(topic, do_select) {}

    this.add_association = function(assoc, refresh_canvas) {}

    this.update_topic = function(topic, refresh_canvas) {}

    this.update_association = function(assoc, refresh_canvas) {}

    this.remove_topic = function(topic_id, refresh_canvas) {}

    /**
     * Removes an association from the canvas (model) and optionally refreshes the canvas (view).
     * If the association is not present on the canvas nothing is performed.
     *
     * @param   refresh_canvas  Optional - if true, the canvas is refreshed.
     */
    this.remove_association = function(assoc_id, refresh_canvas) {}

    /**
     * Clears the model: removes all topics and associations. Resets the selection and translation.
     */
    this.clear = function() {}

    // === Selection ===

    /**
     * @return  an object with "select" and "display" properties (both values are Topic objects).
     */
    this.select_topic = function(topic_id) {}

    this.select_association = function(assoc_id) {}

    this.reset_selection = function(refresh_canvas) {}

    // ---

    this.scroll_topic_to_center = function(topic_id) {}

    this.begin_association = function(topic_id, x, y) {}

    // ### FIXME: get_associations()?

    this.refresh = function() {}

    this.close_context_menu = function() {}

    // === Grid Positioning ===

    this.start_grid_positioning = function() {}

    this.stop_grid_positioning = function() {}

    // === Left SplitPanel Component ===

    /**
     * Called by the SplitPanel once this renderer has been added to the DOM.
     */
    this.init = function() {}

    /**
     * @param   size    an object with "width" and "height" properties.
     */
    this.resize = function(size) {}

    this.resize_end = function() {}
}
