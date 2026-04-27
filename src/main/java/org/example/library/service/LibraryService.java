package org.example.library.service;

import org.example.library.model.Book;
import org.example.library.model.Reader;
import org.example.library.model.Rental;
import org.example.library.persistence.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;

public class LibraryService {

    public List<Book> getAllBooks() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Book", Book.class).list();
        }
    }

    public List<Reader> getAllReaders() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Reader", Reader.class).list();
        }
    }

    public List<Rental> getAllRentals() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Rental r order by r.issueDate desc", Rental.class).list();
        }
    }

    public List<Book> getAvailableBooks() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("""
                    from Book b
                    where b.id not in (
                        select r.book.id from Rental r where r.actualReturnDate is null
                    )
                    """, Book.class).list();
        }
    }

    public void addBook(String title, String author, String genre, double depositCost, double rentalCost) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);
        book.setDepositCost(depositCost);
        book.setRentalCost(rentalCost);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(book);
            tx.commit();
        }
    }

    public void addReader(String lastName, String firstName, String middleName, String address, String phone) {
        Reader reader = new Reader();
        reader.setLastName(lastName);
        reader.setFirstName(firstName);
        reader.setMiddleName(middleName);
        reader.setAddress(address);
        reader.setPhone(phone);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(reader);
            tx.commit();
        }
    }

    public void issueBook(long bookId, long readerId, LocalDate issueDate, LocalDate expectedReturnDate) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            Book book = session.get(Book.class, bookId);
            Reader reader = session.get(Reader.class, readerId);

            Rental rental = new Rental();
            rental.setBook(book);
            rental.setReader(reader);
            rental.setIssueDate(issueDate);
            rental.setExpectedReturnDate(expectedReturnDate);

            session.persist(rental);
            tx.commit();
        }
    }

    public void returnBook(long rentalId, LocalDate returnDate) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Rental rental = session.get(Rental.class, rentalId);

            if (rental != null) {
                rental.setActualReturnDate(returnDate);
                session.merge(rental);
            }

            tx.commit();
        }
    }

    public FinanceStats calculateFinanceStats() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Double earned = session.createQuery("""
                    select coalesce(sum(r.book.rentalCost), 0)
                    from Rental r
                    where r.actualReturnDate is not null
                    """, Double.class).getSingleResult();

            Double deposits = session.createQuery("""
                    select coalesce(sum(r.book.depositCost), 0)
                    from Rental r
                    where r.actualReturnDate is null
                    """, Double.class).getSingleResult();

            return new FinanceStats(earned, deposits);
        }
    }

    public void seedDataIfEmpty() {
        if (!getAllBooks().isEmpty()) {
            return;
        }

        addBook("Кобзар", "Тарас Шевченко", "Поезія", 500, 50);
        addBook("Тигролови", "Іван Багряний", "Роман", 450, 45);
        addBook("Лісова пісня", "Леся Українка", "Драма", 400, 40);

        addReader("Іваненко", "Петро", "Сергійович", "Київ", "+380501112233");
        addReader("Шевчук", "Олена", "Ігорівна", "Львів", "+380671234567");
    }
}
