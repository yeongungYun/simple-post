package com.posts.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

class IdConverterTest {

    private IdConverter<Long> idConverter;

    @BeforeEach
    public void init() {
        idConverter = new IdConverter<>();
    }


    @Test
    @DisplayName("id를 Map 형식으로 반환")
    void idConvert() {
        // given
        String KEY = "id";
        Long id = 1_000L;

        // when
        Map<String, Long> result = idConverter.convert(id);

        // then
        Assertions.assertThat(result.get(KEY)).isEqualTo(id);
    }
}