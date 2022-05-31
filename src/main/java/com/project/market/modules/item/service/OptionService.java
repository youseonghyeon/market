package com.project.market.modules.item.service;

import com.project.market.modules.item.repository.OptionContentRepository;
import com.project.market.modules.item.repository.OptionTitleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OptionService {

    private final OptionTitleRepository optionTitleRepository;
    private final OptionContentRepository optionContentRepository;
}
