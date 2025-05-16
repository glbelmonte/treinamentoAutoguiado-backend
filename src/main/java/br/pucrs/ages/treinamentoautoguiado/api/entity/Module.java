package br.pucrs.ages.treinamentoautoguiado.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity(name = "modules")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Module {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, name = "module_order")
	private Integer order;

	@OneToMany(mappedBy = "module")
	private List<ModuleItem> moduleItems;

	@JsonIgnore
	@Transient
	private String progress;

	public Integer getModuleOrder() {
		return order;
	}
}
