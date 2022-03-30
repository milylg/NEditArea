package com.milylg.editarea.ui;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Paragraphs {
    private List<Node> nodes;

    public Paragraphs() {
        this.nodes = new ArrayList<>();
    }

    public void add(@NonNull Node node) {
        nodes.add(node);
    }
}

