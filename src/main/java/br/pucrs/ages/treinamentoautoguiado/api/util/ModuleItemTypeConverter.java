package br.pucrs.ages.treinamentoautoguiado.api.util;

import br.pucrs.ages.treinamentoautoguiado.api.model.ModuleItemType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ModuleItemTypeConverter implements AttributeConverter<ModuleItemType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ModuleItemType attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public ModuleItemType convertToEntityAttribute(Integer dbData) {
        if (dbData == null) return null;
        for (ModuleItemType type : ModuleItemType.values()) {
            if (type.getValue() == dbData) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown database value: " + dbData);
    }
}