package br.pucrs.ages.treinamentoautoguiado.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModuleItemType {

    TITLE(1),
    SUBTITLE(2),
    FEEDBACK(3),
    VIDEO(4),
    QUESTION(5),
    TEXT(6),
    TEXT_OUTLINED(7),
    AUDIO(8),
    QUESTION_SINGLE_CHOICE(9),
    QUESTION_MULTIPLE_CHOICE(10),
    RATING(11),
    SUBMIT(12),
    DUMMY(13);

    private final int value;
}

