package za.co.quadrantsystems.enums;
import lombok.Getter;
@Getter
public enum ErrorCodes {
	  UNKNOWN("0", " ", "Success."),
	    //    CODE_101("101", "MS03", "Account on Name Inquiry cannot be verified. "),
	    CODE_100("100", "MS03", "Internal processing error."),
	    CODE_500("500", "AB05/TM01", "Timeout error. The code reported at sender is AB05 and the coder reported at receiver is TM01."),
	    CODE_501("501", "MS02", "Payment rejected by receiver Participant."),
	    CODE_502("502", "AB08", "Receiver Participant is disconnected."),
	    CODE_1000("1000", "MS03", "Generic error."),
	    CODE_1001("1001", "MS03", "Generic validation error."),
	    CODE_1002("1002", "FF01", "The currency is invalid."),
	    CODE_1003("1003", "FF01", "The amount is invalid (less then or equal to zero). "),
	    CODE_1004("1004", "DNOR", "Value of field DbtrAgt is invalid: No active Participant with this BIC was found.  The value is different then InstgAgt.  The value is different then the sender Participant detected at the communication channel level."),
	    CODE_1005("1005", "CNOR", "Value of field CdtrAgt is invalid: No active Participant with this BIC was found."),
	    CODE_1006("1006", "RC01", "Value of field InstgAgt is invalid:  No active Participant with this BIC was found.  The value is different then the sender Participant detected at the communication channel level."),
	    CODE_1007("1007", "RC01", "Unused"),
	    CODE_1008("1008", "AC01", "The debtor IBAN is invalid."),
	    CODE_1009("1009", "AC01", "The creditor IBAN is invalid."),
	    CODE_1010("1010", "AM05", "A transaction with the same reference has been detected within processing."),
	    CODE_1011("1011", "AM05", "A transaction with the same reference has been detected as already processed."),
	    CODE_1012("1012", "FF01", "Settlement date is invalid (IntrBkSttlmDt)."),
	    CODE_1013("1013", "FF01", "Invalid XML format."),
	    CODE_1014("1014", "", "Unused."),
	    CODE_1015("1015", "AB05", "Invalid time – the value of field Acceptance DataTime is older then the current time with the timeout parameter value configured in the payment schema."),
	    CODE_1016("1016", "AG09", "Invalid original reference – during the processing of a pacs.028 message, no original transaction with that reference was found to have been processed within the last 24 hours. "),
	    CODE_1017("1017", "AG09", "Invalid status of the original transaction – during the processing of a pacs.002/pacs.028, the referred transaction has no appropriate status (message: WAIT_RECEIVER, transaction: HOLD)."),
	    CODE_1018("1018", "RC01", "No valid sender – the sender Participant is not ACTIVE."),
	    CODE_1019("1019", "AG01", "The payment schema is not defined for the currency mentioned in the message."),
	    CODE_1020("1020", "AM02", "The amount is too large in comparison to the parameter defined in the payment schema."),
	    CODE_1021("1021", "AG10", "The sender is BLOCKED (temporary deactivated)."),
	    CODE_1022("1022", "RR04", "The Participant is not mapped in the payment schema. Both Participants mentioned in the message need to be checked."),
	    CODE_1026("1026", "FF01", "Invalid XML field. It is setup if the validation of some fields from the XML message do not respect the validation rules that are not included in the XML schema, e.g.: ClrSys, SvcLvl, presence InstdAgt, value NbOfTxs. Message not XML, Reference not valid, 2 cases, reference not start with date or correct date, not followed by PAPSS ID of sender "),
	    CODE_1027("1027", "RC01", "Invalid receiver message field. It could be the AppHdr.To.FIId.FinInstId.BICFI or the Asignee (for camt.* messages)."),
	    CODE_1028("1028", "FF01", "The value of field Original Settlement Date is not valid in comparison to the payment schema and the current day."),
	    CODE_1029("1029", "RC01", "Transaction/message OnUs. Debtor Agent and Creditor Agent are equal."),
	    CODE_1030("1030", "RC01", "Invalid originator. The sender of the payment is not the instigated agent."),
	    CODE_1031("1031", "MS03", "Invalid national payment message. In case of an escro reservation the the sender country must be the same as the receiver country."),
	    CODE_1032("1032", "MS03", "Missing sender Settlement Region. No settlement region was found for the sender country and currency."),
	    CODE_1033("1033", "MS03", "Missing instructed amount."),
	    CODE_1034("1034", "MS05", "Missing receiver Settlement Region."),
	    CODE_1035("1035", "MS03", "Missing FX Rate."),
	    CODE_1036("1036", "MS05", "Account disabled."),
	    CODE_1037("1037", "MS03", "Invalid sender agent."),
	    CODE_1038("1038", "MS03", "Compliance check failure."),
	    CODE_2000("2000", "AM23", "Insufficient funds for the transaction clearing."),
	    CODE_3000("3000", "FF01", "The Participant has no ACTIVE registered DS certificate for the validation of messages."),
	    CODE_3001("3001", "FF01", "The XML message does not entail the digital signature in the specified format."),
	    CODE_3002("3002", "FF01", "The digital signature does not protect the entire XML message."),
	    CODE_3003("3003", "FF01", "Invalid digital signature."),
	    CODE_3004("3004", "FF01", "The certificate used for the signature is expired or it has been revoked."),
	    CODE_9001("9001", "MS03", "Settlement posting failure.");

	    private String value;
	    private String mapping;
	    private String description;

	    ErrorCodes(String value, String mapping, String description) {
	        this.value = value;
	        this.mapping = mapping;
	        this.description = description;
	    }

}
