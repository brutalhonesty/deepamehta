package de.deepamehta.plugins.typeeditor.migrations;

import de.deepamehta.core.service.Migration;



public class Migration1 extends Migration {

    // -------------------------------------------------------------------------------------------------- Public Methods

    @Override
    public void run() {
        addTopicTypeSetting("dm4.core.topic_type", "page_renderer_uri", "dm4.typeeditor.topictype_renderer");
    }
}
