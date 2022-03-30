package com.milylg.editarea.ui;

public enum ApplyStyleEnum {
    BOLD {
        @Override
        String apply(String template) {
            return template.replace("{{$content}}", "<b>{{$content}}</b>");
        }
    },
    UNDERLINE {
        @Override
        String apply(String template) {
            return "null";
        }
    },
    H1 {
        @Override
        String apply(String template) {
            return template.replace("{{$tag}}", "h1");
        }
    },
    H2 {
        @Override
        String apply(String template) {
            return template.replace("{{$tag}}", "h2");
        }
    },
    NORMAL {
        @Override
        String apply(String template) {
            return template.replace("{{$tag}}", "p");
        }
    },
    BLOCKQUOTE {
        @Override
        String apply(String template) {
            return template.replace("{{$tag}}", "blockquote");
        }
    },
    ORDER_ITEM {
        @Override
        String apply(String template) {
            return template.replace("{{$tag}}", "span");
        }
    },
    UNORDERED_ITEM {
        @Override
        String apply(String template) {
            return template.replace("{{$tag}}", "span");
        }
    };

    abstract String apply(String template);
}
