package com.example.libraryappv2;

public class CopyDTO {
    private final int id;
    private final BookDTO book;
    private final boolean reference;
    private final boolean onLoan;

    public CopyDTO(int id, BookDTO book, boolean reference, boolean onLoan)
    {
        this.id = id;
        this.book = book;
        this.reference = reference;
        this.onLoan = onLoan;
    }

    public int getId()
    {
        return id;
    }

    public BookDTO getBook()
    {
        return book;
    }

    public String getStatus()
    {
        return isReferenceOnly() ? "Reference only" : (isOnLoan() ? "On loan" : "Available");
    }

    public boolean isOnLoan()
    {
        return onLoan;
    }

    public boolean isReferenceOnly()
    {
        return reference;
    }
}
