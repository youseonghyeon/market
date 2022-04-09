package com.project.market.modules.item.form;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class TagForm {

    Set<String> tags = new HashSet<>();
}
