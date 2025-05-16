package br.pucrs.ages.treinamentoautoguiado.api.entity;

import br.pucrs.ages.treinamentoautoguiado.api.model.ModuleItemType;
import br.pucrs.ages.treinamentoautoguiado.api.util.ModuleItemTypeConverter;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "module_items")
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Getter
@Builder
public class ModuleItem {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String text;

	@Column(nullable = false, name = "module_phase")
	private Integer modulePhase;

	@Convert(converter = ModuleItemTypeConverter.class)
	@Column(nullable = false)
	private ModuleItemType type;

	@Column(nullable = false, name = "module_item_order")
	private Integer moduleItemOrder;

	@ManyToOne 
	@JoinColumn(name="module_id") 
	private Module module; 

}