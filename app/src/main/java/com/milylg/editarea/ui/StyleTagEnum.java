package com.milylg.editarea.ui;

public enum StyleTagEnum {
    TABLE_NOORDER_LIST {
        @Override
        String template() {
            return "<ul data-tag=\"ul\">{{$content}}</ul>";
        }
    },
    TABLE_ORDER_LIST {
        @Override
        String template() {
            return "<ol data-tag=\"ol\">{{$content}}</ol>";
        }
    },
    ROW_NOORDER {
        @Override
        String template() {
            return "<li data-tag=\"list-item-ul\"><{{$tag}} {{$style}}>{{$content}}</{{$tag}}></li>";
        }
    },
    ROW_ORDER {
        @Override
        String template() {
            return "<li data-tag=\"list-item-ol\"><{{$tag}} {{$style}}>{{$content}}</{{$tag}}></li>";
        }
    },
    IMG {
        @Override
        String template() {
            return "<div data-tag=\"img\"><img src=\"{{$url}}\" />{{$img-sub}}</div>";
        }
    },
    IMG_SUB {
        @Override
        String template() {
            return "<{{$tag}} data-tag=\"img-sub\" {{$style}} class=\"editor-image-subtitle\">{{$content}}</{{$tag}}>";
        }
    },
    INPUT {
        @Override
        String template() {
            return "<{{$tag}} data-tag=\"input\" {{$style}}>{{$content}}</{{$tag}}>";
        }
    };

    abstract String template();
}
