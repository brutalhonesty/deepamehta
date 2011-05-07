function TextFieldRenderer(topic, field, rel_topics) {

    this.render_field = function() {
        // field label
        dm3c.render.field_label(field)
        // field value
        return js.render_text(field.value)
    }

    this.render_form_element = function() {
        if (!field.rows) {
            alert("WARNING (TextFieldRenderer.render_form_element):\n\nField \"" + field.uri +
                "\" has no \"rows\" setting.\n\nfield=" + JSON.stringify(field))
        } else if (field.rows == 1) {
            var input = dm3c.render.input(topic, field)
            if (field.autocomplete_indexes) {
                var doctype_impl = dm3c.get_doctype_impl(topic)
                input.keyup(doctype_impl.autocomplete)
                input.blur(doctype_impl.lost_focus)
                input.attr({autocomplete: "off"})
            }
            return input
        } else {
            return $("<textarea>").attr({"field-uri": field.uri, rows: field.rows}).text(field.value)
        }
    }

    this.read_form_value = function() {
        return $.trim($("[field-uri=" + field.uri + "]").val())
    }
}