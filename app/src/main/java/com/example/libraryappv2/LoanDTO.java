package com.example.libraryappv2;

import java.util.Calendar;

/**
 *
 * @author gdm1
 * @Edited by Jack Walker
 */
public class LoanDTO{
    private static final int MAX_RENEWALS = 3;
    private final int id;
    private final CopyDTO copy;
    private final Calendar loanDate;
    private Calendar dueDate;
    private Calendar returnDate;
    private int numberOfRenewals;

    public LoanDTO(int id, CopyDTO copy, Calendar loanDate, Calendar dueDate, Calendar returnDate, int numberOfRenewals)
    {
        this.id = id;
        this.copy = copy;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.numberOfRenewals = numberOfRenewals;
    }

    public int getId()
    {
        return id;
    }

    public CopyDTO getCopy()
    {
        return copy;
    }

    public Calendar getLoanDate()
    {
        return loanDate;
    }

    public Calendar getDueDate()
    {
        return dueDate;
    }

    public Calendar getReturnDate()
    {
        return returnDate;
    }

    public int getNumberOfRenewals()
    {
        return numberOfRenewals;
    }

    public boolean isRenewable()
    {
        return numberOfRenewals < MAX_RENEWALS;
    }

    public void setDueDate(Calendar dueDate)
    {
        this.dueDate = dueDate;
    }

    public void setReturnDate(Calendar returnDate)
    {
        this.returnDate = returnDate;
    }

    public void setNumberOfRenewals(int numberOfRenewals)
    {
        this.numberOfRenewals = numberOfRenewals;
    }

}

