import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KasirApp extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField textFieldItem;
    private JComboBox<String> comboBoxCategory;
    private JTextField textFieldPrice;
    private JCheckBox checkBoxDiscount;
    private JTextField textFieldDiscount;
    private JButton addButton;
    private JButton clearButton;
    private JTextArea textAreaReceipt;
    private JRadioButton cashRadioButton; 
    private JRadioButton debitRadioButton; 

    public KasirApp() {
        setTitle("Aplikasi Kasir");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Panel Utama
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Panel Input
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2, 5, 5));

        // Komponen Input
        JLabel labelItem = new JLabel("Barang:");
        textFieldItem = new JTextField();
        JLabel labelCategory = new JLabel("Kategori:");
        comboBoxCategory = new JComboBox<>();
        comboBoxCategory.addItem("Makanan");
        comboBoxCategory.addItem("Minuman");
        comboBoxCategory.addItem("Rokok");
        comboBoxCategory.addItem("Kecantikan");
        JLabel labelPrice = new JLabel("Harga:");
        textFieldPrice = new JTextField();
        JLabel labelDiscount = new JLabel("Diskon:");
        checkBoxDiscount = new JCheckBox("Apply Discount");
        JLabel labelCustomDiscount = new JLabel("Kustom Diskon (%):");
        textFieldDiscount = new JTextField();

        inputPanel.add(labelItem);
        inputPanel.add(textFieldItem);
        inputPanel.add(labelCategory);
        inputPanel.add(comboBoxCategory);
        inputPanel.add(labelPrice);
        inputPanel.add(textFieldPrice);
        inputPanel.add(labelDiscount);
        inputPanel.add(checkBoxDiscount);
        inputPanel.add(labelCustomDiscount);
        inputPanel.add(textFieldDiscount);

        // Panel Tombol
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Tambah Barang");
        clearButton = new JButton("Clear");
        JButton deleteButton = new JButton("Hapus Barang");
        JButton payButton = new JButton("Bayar"); 

        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(deleteButton); 
        buttonPanel.add(payButton); 

        // Panel Receipt
        JPanel receiptPanel = new JPanel();
        receiptPanel.setLayout(new BorderLayout());
        JLabel labelReceipt = new JLabel("Receipt:");
        textAreaReceipt = new JTextArea();
        textAreaReceipt.setEditable(false);
        JScrollPane receiptScrollPane = new JScrollPane(textAreaReceipt);
        receiptPanel.add(labelReceipt, BorderLayout.NORTH);
        receiptPanel.add(receiptScrollPane, BorderLayout.CENTER);

        // Panel Tabel
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());

        // Tabel
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Barang");
        tableModel.addColumn("Kategori");
        tableModel.addColumn("Harga");
        tableModel.addColumn("Diskon");
        tableModel.addColumn("Harga Setelah Diskon");

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Tambahkan listener untuk tombol "Tambah Barang"
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String item = textFieldItem.getText();
                String category = comboBoxCategory.getSelectedItem().toString();
                String price = textFieldPrice.getText();
                boolean applyDiscount = checkBoxDiscount.isSelected();
                double discountPercentage = 0;

                if (applyDiscount) {
                    // Mendapatkan persentase diskon dari input pengguna
                    discountPercentage = Double.parseDouble(textFieldDiscount.getText());
                }

                // Menghitung harga setelah diterapkan diskon
                double priceValue = Double.parseDouble(price);
                double discountedPrice = applyDiscount ? priceValue - (discountPercentage / 100) * priceValue : priceValue;

                // Tambahkan data ke tabel
                String[] rowData = { item, category, price, Double.toString(discountPercentage),
                        Double.toString(discountedPrice) };
                tableModel.addRow(rowData);

                clearFields();
                updateReceipt();
            }
        });

        // Tambahkan listener untuk tombol "Clear"
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });

        // Tambahkan listener untuk tombol "Hapus Barang"
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) { // Pastikan ada baris yang dipilih
                    tableModel.removeRow(selectedRow);
                    updateReceipt();
                }
            }
        });

        // Tambahkan listener untuk tombol "Bayar"
        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Membuat dialog untuk memilih metode pembayaran
                JPanel paymentPanel = new JPanel();
                paymentPanel.setLayout(new GridLayout(2, 1));
                cashRadioButton = new JRadioButton("Cash");
                debitRadioButton = new JRadioButton("Debit");
                ButtonGroup paymentGroup = new ButtonGroup();
                paymentGroup.add(cashRadioButton);
                paymentGroup.add(debitRadioButton);
                paymentPanel.add(cashRadioButton);
                paymentPanel.add(debitRadioButton);

                // Tampilkan dialog pembayaran
                int option = JOptionPane.showOptionDialog(null, paymentPanel, "Metode Pembayaran",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

                if (option == JOptionPane.OK_OPTION) {
                    if (cashRadioButton.isSelected()) {
                        // Pembayaran menggunakan cash
                        handleCashPayment();
                    } else if (debitRadioButton.isSelected()) {
                        // Pembayaran menggunakan debit
                        handleDebitPayment();
                    } else {
                        JOptionPane.showMessageDialog(null, "Pilih metode pembayaran!");
                    }
                }
            }
        });

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(tablePanel, BorderLayout.WEST);
        mainPanel.add(receiptPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private void updateReceipt() {
        textAreaReceipt.setText("");
        double totalHarga = 0;

        for (int row = 0; row < tableModel.getRowCount(); row++) {
            String item = tableModel.getValueAt(row, 0).toString();
            String category = tableModel.getValueAt(row, 1).toString();
            String price = tableModel.getValueAt(row, 2).toString();
            String discount = tableModel.getValueAt(row, 3).toString();
            String discountedPrice = tableModel.getValueAt(row, 4).toString();

            textAreaReceipt.append("Barang: " + item + "\n");
            textAreaReceipt.append("Kategori: " + category + "\n");
            textAreaReceipt.append("Harga: " + price + "\n");
            textAreaReceipt.append("Diskon: " + discount + "% \n");
            textAreaReceipt.append("Harga Setelah Diskon: " + discountedPrice + "\n");
            textAreaReceipt.append("-------------------------\n");

            // Menambahkan harga setelah diskon ke total harga
            double itemPrice = Double.parseDouble(discountedPrice);
            totalHarga += itemPrice;
        }

        // Menampilkan total harga
        textAreaReceipt.append("Total Harga: " + totalHarga + "\n");
    }

    private void handleCashPayment() {
        // Mengambil total uang yang diinput pengguna
        String totalUangStr = JOptionPane.showInputDialog("Masukkan Total Uang:");
        double totalUang = Double.parseDouble(totalUangStr);

        // Menghitung total harga barang dalam tabel (menggunakan harga setelah diskon)
        double totalHarga = 0;
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            String discountedPriceStr = tableModel.getValueAt(row, 4).toString();
            double discountedPrice = Double.parseDouble(discountedPriceStr);
            totalHarga += discountedPrice;
        }

        // Menghitung kembalian atau menampilkan pesan jika uang kurang
        if (totalUang >= totalHarga) {
            double kembalian = totalUang - totalHarga;
            JOptionPane.showMessageDialog(null, "Pembayaran Berhasil!\nKembalian: " + kembalian);
            clearFields();
            tableModel.setRowCount(0); // Menghapus semua data barang dari tabel
            updateReceipt();
        } else {
            JOptionPane.showMessageDialog(null, "Uang Anda Kurang!");
        }
    }

    private void handleDebitPayment() {
        // Mengambil total harga barang dalam tabel (menggunakan harga setelah diskon)
        double totalHarga = 0;
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            String discountedPriceStr = tableModel.getValueAt(row, 4).toString();
            double discountedPrice = Double.parseDouble(discountedPriceStr);
            totalHarga += discountedPrice;
        }

        JOptionPane.showMessageDialog(null, "Pembayaran Berhasil!\nTotal Harga: " + totalHarga);
        clearFields();
        tableModel.setRowCount(0); // Menghapus semua data barang dari tabel
        updateReceipt();
    }
    
    private void clearFields() {
        textFieldItem.setText("");
        comboBoxCategory.setSelectedIndex(0);
        textFieldPrice.setText("");
        checkBoxDiscount.setSelected(false);
        textFieldDiscount.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new KasirApp();
            }
        }); 
    }
}
