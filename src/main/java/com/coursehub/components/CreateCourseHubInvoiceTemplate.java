//package com.coursehub.components;
//
//import com.itextpdf.forms.PdfAcroForm;
//import com.itextpdf.forms.fields.PdfTextFormField;
//import com.itextpdf.io.font.constants.StandardFonts;
//import com.itextpdf.kernel.colors.ColorConstants;
//import com.itextpdf.kernel.colors.DeviceRgb;
//import com.itextpdf.kernel.font.PdfFont;
//import com.itextpdf.kernel.font.PdfFontFactory;
//import com.itextpdf.kernel.geom.Rectangle;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
//import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.borders.Border;
//import com.itextpdf.layout.element.*;
//import com.itextpdf.layout.properties.*;
//import com.itextpdf.layout.renderer.CellRenderer;
//import com.itextpdf.layout.renderer.DrawContext;
//
//import java.io.File;
//import java.io.FileOutputStream;
//
//public class CreateCourseHubInvoiceTemplate {
//
//    private static final DeviceRgb PRIMARY_COLOR = new DeviceRgb(41, 128, 185);
//    private static final DeviceRgb SECONDARY_COLOR = new DeviceRgb(52, 73, 94);
//    private static final DeviceRgb LIGHT_GRAY = new DeviceRgb(236, 240, 241);
//    private static final DeviceRgb ACCENT_COLOR = new DeviceRgb(39, 174, 96);
//
//    public static void main(String[] args) throws Exception {
//        String templateDir = "src/main/resources/templates/";
//        File directory = new File(templateDir);
//        if (!directory.exists()) {
//            directory.mkdirs();
//        }
//
//        PdfWriter writer = new PdfWriter(new FileOutputStream(templateDir + "invoice_template_coursehub.pdf"));
//        PdfDocument pdf = new PdfDocument(writer);
//        Document document = new Document(pdf);
//
//        PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
//        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
//
//        // Add header background
//        addHeaderBackground(pdf, document);
//
//        // Header table
//        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
//        headerTable.setWidth(UnitValue.createPercentValue(100));
//
//        Cell leftHeaderCell = new Cell()
//                .add(new Paragraph("CourseHub")
//                        .setFont(boldFont).setFontSize(32).setFontColor(ColorConstants.WHITE))
//                .add(new Paragraph("Professional Online Learning Platform")
//                        .setFont(regularFont).setFontSize(12).setFontColor(ColorConstants.WHITE))
//                .setBorder(null).setPadding(10).setPaddingLeft(40);
//        headerTable.addCell(leftHeaderCell);
//
//        Cell rightHeaderCell = new Cell()
//                .add(new Paragraph("CourseHub Education Ltd.")
//                        .setFont(boldFont).setFontSize(14).setFontColor(ColorConstants.WHITE))
//                .add(new Paragraph("Km29 - Hoa Lac, Thach That\nHanoi, Vietnam 100000")
//                        .setFont(regularFont).setFontSize(10).setFontColor(ColorConstants.WHITE))
//                .add(new Paragraph("Email: it4beginer@gmail.com")
//                        .setFont(regularFont).setFontSize(10).setFontColor(ColorConstants.WHITE))
//                .add(new Paragraph("Phone: +84 982226376")
//                        .setFont(regularFont).setFontSize(10).setFontColor(ColorConstants.WHITE))
//                .setTextAlignment(TextAlignment.RIGHT)
//                .setBorder(null).setPadding(10).setPaddingRight(40);
//        headerTable.addCell(rightHeaderCell);
//
//        document.add(headerTable);
//        document.add(new Paragraph("\n"));
//
//        // Horizontal line
//        SolidLine line = new SolidLine(1f);
//        line.setColor(PRIMARY_COLOR);
//        document.add(new LineSeparator(line).setMarginTop(5).setMarginBottom(5));
//
//        // Create form
//        PdfAcroForm.getAcroForm(pdf, true); // ensure form is created
//
//        Div formContainer = new Div();
//        formContainer.setWidth(UnitValue.createPercentValue(100));
//        formContainer.setBackgroundColor(LIGHT_GRAY);
//        formContainer.setPadding(10).setMarginTop(5);
//
//        Table formTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}));
//        formTable.setWidth(UnitValue.createPercentValue(100));
//
//        addFormRow(formTable, "Course Name:", "field_courseName", pdf, regularFont);
//        addFormRow(formTable, "Transaction Code:", "field_transactionCode", pdf, regularFont);
//        addFormRow(formTable, "Purchase Date & Time:", "field_time", pdf, regularFont);
//        addFormRow(formTable, "Payment Method:", "field_paymentMethod", pdf, regularFont);
//        addFormRow(formTable, "Total Amount:", "field_totalAmount", pdf, regularFont);
//
//        formContainer.add(formTable);
//        document.add(formContainer);
//
//        // Terms & Conditions
//        document.add(new Paragraph("Terms & Conditions:")
//                .setFont(boldFont).setFontSize(12).setFontColor(SECONDARY_COLOR).setMarginTop(10));
//
//        document.add(new Paragraph("• Course access is provided immediately after payment confirmation\n" +
//                "• All sales are final - no refunds after course access is granted\n" +
//                "• Course materials are for personal use only\n" +
//                "• Technical support is available 24/7 via email or chat\n" +
//                "• Certificate will be issued upon successful course completion")
//                .setFont(regularFont).setFontSize(9).setFontColor(SECONDARY_COLOR).setMarginTop(5));
//
//        // Thank you message
//        document.add(new Paragraph("Thank you for choosing CourseHub!")
//                .setFont(boldFont).setFontSize(14).setFontColor(PRIMARY_COLOR)
//                .setTextAlignment(TextAlignment.CENTER).setMarginTop(10));
//
//        document.close();
//        System.out.println("Invoice template with visible fields created at: " + templateDir);
//    }
//
//    private static void addHeaderBackground(PdfDocument pdf, Document document) {
//        PdfCanvas canvas = new PdfCanvas(pdf.addNewPage());
//        canvas.setFillColor(PRIMARY_COLOR);
//        canvas.rectangle(0, pdf.getDefaultPageSize().getHeight() - 160,
//                pdf.getDefaultPageSize().getWidth(), 160);
//        canvas.fill();
//        document.setTopMargin(40); // giảm lề trên để kéo nội dung lên
//    }
//
//    private static void addFormRow(Table table, String label, String fieldName, PdfDocument pdf, PdfFont font) {
//        Cell labelCell = new Cell()
//                .add(new Paragraph(label).setFont(font).setFontSize(11).setFontColor(SECONDARY_COLOR))
//                .setBackgroundColor(LIGHT_GRAY).setPadding(5).setBorder(Border.NO_BORDER);
//        table.addCell(labelCell);
//
//        Cell fieldCell = new Cell()
//                .setPadding(5)
//                .setBackgroundColor(ColorConstants.WHITE)
//                .setBorder(Border.NO_BORDER);
//        fieldCell.setNextRenderer(new TextFieldCellRenderer(fieldCell, fieldName, pdf));
//        table.addCell(fieldCell);
//    }
//
//    // Renderer class để render text field trong cell
//    private static class TextFieldCellRenderer extends CellRenderer {
//        protected String fieldName;
//        protected PdfDocument pdf;
//
//        public TextFieldCellRenderer(Cell modelElement, String fieldName, PdfDocument pdf) {
//            super(modelElement);
//            this.fieldName = fieldName;
//            this.pdf = pdf;
//        }
//
//        @Override
//        public void draw(DrawContext drawContext) {
//            super.draw(drawContext);
//            Rectangle rect = getOccupiedAreaBBox().clone();
//            rect.setX(rect.getX() + 2); // padding
//            rect.setWidth(rect.getWidth() - 4);
//            PdfTextFormField field = PdfTextFormField.createText(
//                    drawContext.getDocument(), rect, fieldName, "");
//            field.setFontSize(11);
//            PdfAcroForm.getAcroForm(drawContext.getDocument(), true).addField(field);
//        }
//    }
//}
