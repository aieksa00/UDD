package UDD.AleksaColovic.SearchEngine.dto;

public record ContractDTO(
        String id,
        String signerName,
        String signerSurname,
        String governmentName,
        String administrationLevel,
        String address,
        String content,
        String fileName
) {

}
