package com.db.awmd.challenge.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferAmountRequest {
	
	@NotNull
	@NotEmpty
	private String accountFromId;
	
	@NotNull
	@NotEmpty
	private String accountToId;
	
	@NotNull
	@Range(min = 1, message = "Amount to be transfered should be positive number.")
	private Integer amount;

}
