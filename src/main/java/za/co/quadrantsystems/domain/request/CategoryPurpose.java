package za.co.quadrantsystems.domain.request;

public enum CategoryPurpose {

//    SUPP("Invoice_Payment""), "SUPP""), "RCPT")"),
//    GOVT("GOVT""), "GOVT""), "");

    SUPP("Invoice Payment"),
    CASH("Cash Payment"),
    CCRD("Credit Card Payment"),
  CORT("Trade Settlement Payment"),
  DCRD("Debit Card Payment"),
  DIVI("Dividend"),
  GOVT("Government Payment"),
  HEDG("Hedging"),
  INTC("Intra Company Payment"),
  INTE("Interest"),
  LOAN("Loan"),
  PENS("Pension Payment"),
  SALA("Salary payment"),
  SECU("Securities"),
  SSBE("Social Security Benefit"),
  TAXS("Tax Payment"),
  TRAD("Trade"),
  TREA("Treasury Payment"),
  VATX("Value Added Tax Payment"),
  WHLD("With Holding");

    public String description;

    CategoryPurpose(final String description) {
        this.description = description;
    }

    public static CategoryPurpose findByValue(String value) {
             CategoryPurpose result = null;
              for (CategoryPurpose code : values()) {
                  if (code.name().equalsIgnoreCase(value)) {
                      result = code;
                      break;
                  }
              }
              return result;
    }

//    CategoryPurpose(final String description"), final String categoryPurpose"), final String purposeCode) {
//        this.description = description;
//        this.categoryPurpose = categoryPurpose;
//        this.purposeCode = purposeCode;
//    }
}
