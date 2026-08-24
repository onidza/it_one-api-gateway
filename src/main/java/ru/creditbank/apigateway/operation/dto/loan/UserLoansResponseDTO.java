package ru.creditbank.apigateway.operation.dto.loan;

import java.util.List;

public record UserLoansResponseDTO(
    List<LoanInfoDTO> loans
) {

}
