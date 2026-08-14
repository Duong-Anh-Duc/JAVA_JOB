package com.example.cv.common.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaginationServiceTest {
    @Test
    void returnsExpectedPageMetadataAndItems() {
        var page = PaginationService.page(List.of("a", "b", "c"), 2, 2, null);

        assertThat(page.current()).isEqualTo(2);
        assertThat(page.pageSize()).isEqualTo(2);
        assertThat(page.pages()).isEqualTo(2);
        assertThat(page.total()).isEqualTo(3);
        assertThat(page.result()).containsExactly("c");
    }
}
