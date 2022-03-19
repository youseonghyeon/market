package com.project.market.modules.item.form;

import com.project.market.modules.item.entity.Tag;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TagForm {

    List<String> tags = new ArrayList<>();
}
