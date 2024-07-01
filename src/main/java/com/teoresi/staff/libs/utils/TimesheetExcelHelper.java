package com.teoresi.staff.libs.utils;

import com.teoresi.staff.dtos.old.ImportTimesheetDTO;
import com.teoresi.staff.libs.data.models.TimesheetExcel;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TimesheetExcelHelper {

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private final Sinks.Many<ImportTimesheetDTO> importTimesheetSink;

    public TimesheetExcelHelper(Sinks.Many<ImportTimesheetDTO> importTimesheetSink) {
        this.importTimesheetSink = importTimesheetSink;
    }

    public boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public List<TimesheetExcel> excelToTimesheets(InputStream is, ImportTimesheetDTO importTimesheetDTO) {
        try (Workbook workbook = new XSSFWorkbook(is)) {

            List<TimesheetExcel> timesheetExcelList = new ArrayList<>();

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);

                Iterator<Row> rows = sheet.iterator();
                List<Row> rowList = IteratorUtils.toList(rows);

                importTimesheetDTO.setTotalRows(rowList.size());
                this.importTimesheetSink.tryEmitNext(importTimesheetDTO);

                int rowNumber = 0;
                for (Row currentRow : rowList) {

                    if (rowNumber == 0) {
                        rowNumber++;
                        continue;
                    }

                    Iterator<Cell> cellsInRow = currentRow.iterator();
                    TimesheetExcel timesheetExcel = new TimesheetExcel();

                    int cellIdx = 0;
                    while (cellsInRow.hasNext()) {
                        Cell currentCell = cellsInRow.next();

                        switch (cellIdx) {
                            case 1:
                                if (currentCell.getCellType() == CellType.STRING) {
                                    timesheetExcel.setUnitTrigram(currentCell.getStringCellValue());
                                }
                                break;

                            case 3:
                                if (currentCell.getCellType() == CellType.STRING) {
                                    timesheetExcel.setProjectId(currentCell.getStringCellValue());
                                } else if (currentCell.getCellType() == CellType.NUMERIC) {
                                    double cellValue = currentCell.getNumericCellValue();
                                    int cellValueInt = (int) cellValue;
                                    timesheetExcel.setProjectId(String.valueOf(cellValueInt));
                                }
                                break;

                            case 4:
                                if (currentCell.getCellType() == CellType.STRING) {
                                    timesheetExcel.setProjectName(currentCell.getStringCellValue());
                                }
                                break;

                            case 12:
                                if (currentCell.getCellType() == CellType.STRING) {
                                    String resource = currentCell.getStringCellValue();
                                    String[] split = resource.split(" - ");
                                    if (split.length == 2) {
                                        timesheetExcel.setResourceName(split[0]);
                                        timesheetExcel.setResourceEmployeeId(Integer.parseInt(split[1]));
                                    }
                                }
                                break;

                            case 14:
                                if (currentCell.getCellType() == CellType.STRING) {
                                    timesheetExcel.setStartDate(parseDate(currentCell.getStringCellValue(), "dd MMM yyyy"));
                                } else if (currentCell.getCellType() == CellType.NUMERIC) {
                                    double cellValue = currentCell.getNumericCellValue();
                                    timesheetExcel.setStartDate(convertExcelDateToJavaDate(cellValue));
                                }
                                break;

                            case 15:
                                if (currentCell.getCellType() == CellType.STRING) {
                                    timesheetExcel.setEndDate(parseDate(currentCell.getStringCellValue(), "dd MMM yyyy"));
                                } else if (currentCell.getCellType() == CellType.NUMERIC) {
                                    double cellValue = currentCell.getNumericCellValue();
                                    timesheetExcel.setEndDate(convertExcelDateToJavaDate(cellValue));
                                }
                                break;

                            case 20:
                                if (currentCell.getCellType() == CellType.STRING) {
                                    timesheetExcel.setMonth(parseDate(currentCell.getStringCellValue(), "dd/MM/yyyy"));
                                } else if (currentCell.getCellType() == CellType.NUMERIC) {
                                    Date date = DateUtil.getJavaDate(currentCell.getNumericCellValue());
                                    LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                    timesheetExcel.setMonth(localDate);
                                }
                                break;

                            case 21:
                                if (currentCell.getCellType() == CellType.NUMERIC) {
                                    double cellValue = currentCell.getNumericCellValue();
                                    timesheetExcel.setHours((int) Math.round(cellValue));
                                }
                                break;

                            default:
                                break;
                        }

                        cellIdx++;
                    }

                    timesheetExcelList.add(timesheetExcel);
                    rowNumber++;
                }
            }

            return timesheetExcelList;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage(), e);
        }
    }


    private LocalDate parseDate(String date, String  pattern) {
        if (StringUtils.hasText(date) && !date.isEmpty()) {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern, Locale.US));
        }
        return null;
    }

    private static LocalDate convertExcelDateToJavaDate(double excelDateValue) {
        // Excel date starts from January 1st, 1900, while Unix time (Java's reference time) starts from January 1st, 1970.
        // There is also a 1-day offset between Excel's representation and Unix time's representation.
        long excelStartDate = -2209161600000L; // January 1st, 1900 00:00:00 in milliseconds since Unix epoch
        long javaTime = (long)((excelDateValue - 1) * 24 * 60 * 60 * 1000) + excelStartDate;

        return LocalDate.ofEpochDay(javaTime / (24 * 60 * 60 * 1000));
    }

}

