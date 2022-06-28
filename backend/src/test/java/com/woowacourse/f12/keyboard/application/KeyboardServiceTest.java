package com.woowacourse.f12.keyboard.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.woowacourse.f12.keyboard.domain.Keyboard;
import com.woowacourse.f12.keyboard.domain.KeyboardRepository;
import com.woowacourse.f12.keyboard.dto.response.KeyboardResponse;
import com.woowacourse.f12.keyboard.exception.KeyboardNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KeyboardServiceTest {

    @Mock
    private KeyboardRepository keyboardRepository;

    @InjectMocks
    private KeyboardService keyboardService;

    @Test
    void id_값으로_키보드를_조회한다() {
        // given
        Keyboard keyboard = Keyboard.builder()
                .id(1L)
                .name("키보드")
                .build();

        given(keyboardRepository.findById(anyLong()))
                .willReturn(Optional.ofNullable(keyboard));
        // when
        KeyboardResponse keyboardResponse = keyboardService.findById(1L);

        // then
        assertAll(
                () -> verify(keyboardRepository).findById(1L),
                () -> assertThat(keyboardResponse.getId()).isEqualTo(1L),
                () -> assertThat(keyboardResponse.getName()).isEqualTo("키보드")
        );
    }

    @Test
    void 존재하지_않는_id_값으로_키보드를_조회하면_예외를_반환한다() {
        // given
        given(keyboardRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        // when then
        assertAll(
                () -> assertThatThrownBy(() -> keyboardService.findById(1L))
                        .isExactlyInstanceOf(KeyboardNotFoundException.class),
                () -> verify(keyboardRepository).findById(1L)
        );
    }
}