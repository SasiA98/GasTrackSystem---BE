package com.teoresi.staff.libs.utils;

import com.teoresi.staff.dtos.ImportResourceDTO;
import com.teoresi.staff.libs.data.models.ResourceExcel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


@Service
public class ResourceExcelHelper {

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private final Sinks.Many<ImportResourceDTO> importResourceSink;

    private static final int UNIT_TRIGRAM_CELL = 0;
    private static final int EMPLOYEE_ID_CELL = 1;
    private static final int SURNAME_CELL = 2;
    private static final int NAME_CELL = 3;
    private static final int BIRTH_DATE_CELL = 6;
    private static final int HIRING_DATE_CELL = 7;
    private static final int LEAVE_DATE_CELL = 8;
    private static final int SITE_CELL = 9;
    private static final int LOCATION_CELL = 10;
    private static final int ROLES_CELL = 11;
    private static final int HOURLY_COST_CELL = 12;

    public ResourceExcelHelper(Sinks.Many<ImportResourceDTO> importResourceSink) {
        this.importResourceSink = importResourceSink;
    }

    public boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public List<ResourceExcel> excelToResources(InputStream is, ImportResourceDTO importResourceDTO) {
        try (Workbook workbook = new XSSFWorkbook(is)) {

            List<ResourceExcel> resourceExcelList = new ArrayList<>();
            int totalRows = 0;

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);

                int sheetRows = sheet.getPhysicalNumberOfRows() - 1;
                totalRows += sheetRows;

                resourceExcelList.addAll(processSheet(sheet));
            }

            importResourceDTO.setTotalRows(totalRows);
            this.importResourceSink.tryEmitNext(importResourceDTO);

            return resourceExcelList;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage(), e);
        }
    }


    private List<ResourceExcel> processSheet(Sheet sheet) {
        List<ResourceExcel> resourceExcelList = new ArrayList<>();
        Iterator<Row> rows = sheet.iterator();

        if (rows.hasNext()) {
            rows.next();
        }

        while (rows.hasNext()) {
            Row currentRow = rows.next();
            ResourceExcel resourceExcel = processRow(currentRow);
            resourceExcelList.add(resourceExcel);
        }

        return resourceExcelList;
    }

    private ResourceExcel processRow(Row row) {
        ResourceExcel resourceExcel = new ResourceExcel();

        for (Cell cell : row) {
            processCell(cell, resourceExcel);
        }

        return resourceExcel;
    }

    private void processCell(Cell cell, ResourceExcel resourceExcel) {
        switch (cell.getColumnIndex()) {
            case UNIT_TRIGRAM_CELL:
                if (cell.getCellType() == CellType.STRING)
                    resourceExcel.setUnitTrigram(cell.getStringCellValue());

                break;

            case EMPLOYEE_ID_CELL:
                if (cell.getCellType() == CellType.STRING)
                    resourceExcel.setEmployeeId(Integer.parseInt(cell.getStringCellValue()));
                else if (cell.getCellType() == CellType.NUMERIC)
                    resourceExcel.setEmployeeId((int) cell.getNumericCellValue());

                break;

            case SURNAME_CELL:
                if (cell.getCellType() == CellType.STRING)
                    resourceExcel.setSurname(capitalizeFirstLetter(cell.getStringCellValue().trim()));
                break;

            case NAME_CELL:
                if (cell.getCellType() == CellType.STRING)
                    resourceExcel.setName(capitalizeFirstLetter(cell.getStringCellValue().trim()));

                break;

            case BIRTH_DATE_CELL:
                if (cell.getCellType() == CellType.NUMERIC)
                    resourceExcel.setBirthDate(DateUtil.getJavaDate(cell.getNumericCellValue()));

                break;

            case HIRING_DATE_CELL:
                if (cell.getCellType() == CellType.NUMERIC)
                    resourceExcel.setHiringDate(DateUtil.getJavaDate(cell.getNumericCellValue()));

                break;

            case LEAVE_DATE_CELL:
                if (cell.getCellType() == CellType.NUMERIC) {
                    resourceExcel.setLeaveDate(DateUtil.getJavaDate(cell.getNumericCellValue()));
                }
                break;

            case SITE_CELL:
                if (cell.getCellType() == CellType.STRING)
                    resourceExcel.setSite(cell.getStringCellValue().trim());

                break;

            case LOCATION_CELL:
                if (cell.getCellType() == CellType.STRING)
                    resourceExcel.setLocation(cell.getStringCellValue().trim());

                break;

            case ROLES_CELL:
                if (cell.getCellType() == CellType.STRING){
                    String[] roles = cell.getStringCellValue().split(",");
                    resourceExcel.setRoles(roles);
                }
                break;

            case HOURLY_COST_CELL:
                if (cell.getCellType() == CellType.NUMERIC) {
                    resourceExcel.setHourlyCost((float) cell.getNumericCellValue());

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    resourceExcel.setHourlyCostStartDate(calendar.getTime());
                    Date canditateHcStartDate = calendar.getTime();

                    if(resourceExcel.getHiringDate() != null && resourceExcel.getHiringDate().after(canditateHcStartDate))
                        canditateHcStartDate = resourceExcel.getHiringDate();

                    resourceExcel.setHourlyCostStartDate(canditateHcStartDate);
                }
                break;

            default:
                break;
        }
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String lowerCased = input.toLowerCase();
        return lowerCased.substring(0, 1).toUpperCase() + lowerCased.substring(1);
    }

}

