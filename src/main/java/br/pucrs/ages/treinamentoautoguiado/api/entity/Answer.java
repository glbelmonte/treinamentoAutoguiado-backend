package br.pucrs.ages.treinamentoautoguiado.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity(name = "answers")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Builder
public class Answer extends BaseEntity{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name="USER_ID")
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name="MODULE_ITEMS_ID")
	private ModuleItem modules_items;

	@Column(nullable = false)
	private String content;
}
