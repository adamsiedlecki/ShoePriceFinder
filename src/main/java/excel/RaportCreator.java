package excel;

import data.Offer;
import org.apache.poi.common.usermodel.Hyperlink;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RaportCreator {

    public void saveOffersToExcel(String shoeName, String shoeSize, boolean isMale, List<Offer> offers) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook();
        CreationHelper createHelper = wb.getCreationHelper();
        Sheet sheet = wb.createSheet(shoeName);

        CellStyle headerStyle = wb.createCellStyle();
        addBordersToStyle(headerStyle);
        Font headerFont = wb.createFont();
        headerFont.setColor(Font.COLOR_NORMAL);
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // HEADER
        Row row = sheet.createRow((short) 0);

        Cell cell0 = row.createCell(0);
        cell0.setCellValue("ID");
        cell0.setCellStyle(headerStyle);

        Cell cell1 = row.createCell(1);
        cell1.setCellValue("SHOE NAME");
        cell1.setCellStyle(headerStyle);

        Cell cell2 = row.createCell(2);
        cell2.setCellValue("SHOE PRICE");
        cell2.setCellStyle(headerStyle);

        Cell cell3 = row.createCell(3);
        cell3.setCellValue("ADDRESS");
        cell3.setCellStyle(headerStyle);

        Cell cell4 = row.createCell(4);
        cell4.setCellValue("SHOP NAME");
        cell4.setCellStyle(headerStyle);

        Cell cell5 = row.createCell(5);
        cell5.setCellValue("IMAGE");
        cell5.setCellStyle(headerStyle);

        int rowId = 1;
        for (Offer offer : offers) {
            Row offerRow = sheet.createRow(rowId);
            Cell contentCell0 = offerRow.createCell(0);
            contentCell0.setCellValue(rowId);
            addStyleToContentCell(wb, contentCell0);

            Cell contentCell1 = offerRow.createCell(1);
            contentCell1.setCellValue(offer.getName());
            addStyleToContentCell(wb, contentCell1);

            Cell contentCell2 = offerRow.createCell(2);
            addStyleToContentCell(wb, contentCell2);
            contentCell2.setCellValue(offer.getPrice().toPlainString());

            CellStyle hlinkstyle = wb.createCellStyle();
            addBordersToStyle(hlinkstyle);
            Hyperlink link = createHelper.createHyperlink(HyperlinkType.URL);
            link.setAddress(offer.getUrl());
            Cell urlCell;
            (urlCell = offerRow.createCell(3)).setHyperlink((org.apache.poi.ss.usermodel.Hyperlink) link);
            urlCell.setCellStyle(hlinkstyle);

            Cell contentCell4 = offerRow.createCell(4);
            addStyleToContentCell(wb, contentCell4);
            contentCell4.setCellValue(offer.getShopName());

            Cell imageCell;
            imageCell = offerRow.createCell(5); //).setCellValue(offer.getImageUrl());
            addStyleToContentCell(wb, imageCell);
            writeImageToCell(wb, sheet, 5, rowId, offer.getImageUrl());

            offerRow.setHeight((short) 1000);
            rowId++;
        }
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH mm");
        FileOutputStream fileOut = new FileOutputStream("RAPORT " + shoeName + " " + shoeSize + " " + LocalDateTime.now().format(formatter) + " .xls");
        wb.write(fileOut);
        fileOut.close();
    }

    private void addStyleToContentCell(Workbook wb, Cell cell) {
        CellStyle style = wb.createCellStyle();
        addBordersToStyle(style);
        cell.setCellStyle(style);
    }

    private void addBordersToStyle(CellStyle cellStyle) {
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
    }

    private void writeImageToCell(HSSFWorkbook wb, Sheet sheet, int column, int row, String imageUrl) throws IOException {
        //InputStream inputStream = new FileInputStream("/home/axel/Bilder/Wasserlilien.jpg");
        InputStream inputStream = new URL(imageUrl).openStream();
        //Get the contents of an InputStream as a byte[].
        byte[] bytes = IOUtils.toByteArray(inputStream);
        //Adds a picture to the workbook
        int pictureIdx = wb.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
        //close the input stream
        inputStream.close();
        //Returns an object that handles instantiating concrete classes
        CreationHelper helper = wb.getCreationHelper();
        //Creates the top-level drawing patriarch.
        Drawing drawing = sheet.createDrawingPatriarch();

        //Create an anchor that is attached to the worksheet
        ClientAnchor anchor = helper.createClientAnchor();

        //create an anchor with upper left cell _and_ bottom right cell
        anchor.setCol1(column);
        anchor.setRow1(row);
        anchor.setCol2(column + 1);
        anchor.setRow2(row + 1);

        //Creates a picture
        Picture pict = drawing.createPicture(anchor, pictureIdx);
        //pict.resize(0.1,0.1);

        //Reset the image to the original size
        //pict.resize(); //don't do that. Let the anchor resize the image!
    }
}
