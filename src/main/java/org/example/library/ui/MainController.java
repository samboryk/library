package org.example.library.ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.library.model.Book;
import org.example.library.model.Reader;
import org.example.library.model.Rental;
import org.example.library.service.FinanceStats;
import org.example.library.service.LibraryService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainController {

    private final LibraryService service = new LibraryService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @FXML
    private TableView<Book> booksTable;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, String> genreColumn;
    @FXML
    private TableColumn<Book, Double> depositColumn;
    @FXML
    private TableColumn<Book, Double> rentColumn;

    @FXML
    private TableView<Reader> readersTable;
    @FXML
    private TableColumn<Reader, String> readerLastNameColumn;
    @FXML
    private TableColumn<Reader, String> readerFirstNameColumn;
    @FXML
    private TableColumn<Reader, String> readerMiddleNameColumn;
    @FXML
    private TableColumn<Reader, String> readerAddressColumn;
    @FXML
    private TableColumn<Reader, String> readerPhoneColumn;

    @FXML
    private TableView<Rental> rentalsTable;
    @FXML
    private TableColumn<Rental, String> rentalBookColumn;
    @FXML
    private TableColumn<Rental, String> rentalReaderColumn;
    @FXML
    private TableColumn<Rental, String> rentalIssueDateColumn;
    @FXML
    private TableColumn<Rental, String> rentalExpectedReturnDateColumn;
    @FXML
    private TableColumn<Rental, String> rentalActualReturnDateColumn;

    @FXML
    private TextField bookTitleField;
    @FXML
    private TextField bookAuthorField;
    @FXML
    private TextField bookGenreField;
    @FXML
    private TextField bookDepositField;
    @FXML
    private TextField bookRentField;

    @FXML
    private TextField readerLastNameField;
    @FXML
    private TextField readerFirstNameField;
    @FXML
    private TextField readerMiddleNameField;
    @FXML
    private TextField readerAddressField;
    @FXML
    private TextField readerPhoneField;

    @FXML
    private ComboBox<Book> rentBookBox;
    @FXML
    private ComboBox<Reader> rentReaderBox;
    @FXML
    private DatePicker expectedReturnDatePicker;

    @FXML
    private Label earnedLabel;
    @FXML
    private Label depositLabel;

    @FXML
    public void initialize() {
        setupTables();
        service.seedDataIfEmpty();
        expectedReturnDatePicker.setValue(LocalDate.now().plusDays(14));
        refreshAll();
    }

    private void setupTables() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        depositColumn.setCellValueFactory(new PropertyValueFactory<>("depositCost"));
        rentColumn.setCellValueFactory(new PropertyValueFactory<>("rentalCost"));

        readerLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        readerFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        readerMiddleNameColumn.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        readerAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        readerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        rentalBookColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getBook().toString()));
        rentalReaderColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getReader().toString()));
        rentalIssueDateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getIssueDate().format(formatter)));
        rentalExpectedReturnDateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getExpectedReturnDate().format(formatter)));
        rentalActualReturnDateColumn.setCellValueFactory(data -> {
            String value = "В оренді";
            if (data.getValue().getActualReturnDate() != null) {
                value = data.getValue().getActualReturnDate().format(formatter);
            }
            return new javafx.beans.property.SimpleStringProperty(value);
        });
    }

    @FXML
    private void onAddBook() {
        if (bookTitleField.getText().isBlank() || bookAuthorField.getText().isBlank() || bookGenreField.getText().isBlank()) {
            showError("Заповніть всі поля книги");
            return;
        }

        try {
            double deposit = Double.parseDouble(bookDepositField.getText());
            double rent = Double.parseDouble(bookRentField.getText());
            service.addBook(bookTitleField.getText(), bookAuthorField.getText(), bookGenreField.getText(), deposit, rent);
            clearBookFields();
            refreshAll();
        } catch (NumberFormatException e) {
            showError("Заставна вартість і прокат мають бути числами");
        }
    }

    @FXML
    private void onAddReader() {
        if (readerLastNameField.getText().isBlank() || readerFirstNameField.getText().isBlank() || readerMiddleNameField.getText().isBlank()) {
            showError("Заповніть ПІБ читача");
            return;
        }

        service.addReader(
                readerLastNameField.getText(),
                readerFirstNameField.getText(),
                readerMiddleNameField.getText(),
                readerAddressField.getText(),
                readerPhoneField.getText()
        );

        clearReaderFields();
        refreshAll();
    }

    @FXML
    private void onIssueBook() {
        Book book = rentBookBox.getValue();
        Reader reader = rentReaderBox.getValue();
        LocalDate expected = expectedReturnDatePicker.getValue();

        if (book == null || reader == null || expected == null) {
            showError("Виберіть книгу, читача і дату повернення");
            return;
        }

        service.issueBook(book.getId(), reader.getId(), LocalDate.now(), expected);
        refreshAll();
    }

    @FXML
    private void onReturnBook() {
        Rental rental = rentalsTable.getSelectionModel().getSelectedItem();

        if (rental == null) {
            showError("Оберіть видану книгу у таблиці");
            return;
        }

        if (rental.getActualReturnDate() != null) {
            showError("Ця книга вже повернена");
            return;
        }

        service.returnBook(rental.getId(), LocalDate.now());
        refreshAll();
    }

    private void refreshAll() {
        booksTable.setItems(FXCollections.observableArrayList(service.getAllBooks()));
        readersTable.setItems(FXCollections.observableArrayList(service.getAllReaders()));
        rentalsTable.setItems(FXCollections.observableArrayList(service.getAllRentals()));
        rentBookBox.setItems(FXCollections.observableArrayList(service.getAvailableBooks()));
        rentReaderBox.setItems(FXCollections.observableArrayList(service.getAllReaders()));

        FinanceStats stats = service.calculateFinanceStats();
        earnedLabel.setText(String.format("Зароблено на прокаті: %.2f грн", stats.getEarnedRentals()));
        depositLabel.setText(String.format("Активні застави: %.2f грн", stats.getActiveDeposits()));
    }

    private void clearBookFields() {
        bookTitleField.clear();
        bookAuthorField.clear();
        bookGenreField.clear();
        bookDepositField.clear();
        bookRentField.clear();
    }

    private void clearReaderFields() {
        readerLastNameField.clear();
        readerFirstNameField.clear();
        readerMiddleNameField.clear();
        readerAddressField.clear();
        readerPhoneField.clear();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Помилка");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
