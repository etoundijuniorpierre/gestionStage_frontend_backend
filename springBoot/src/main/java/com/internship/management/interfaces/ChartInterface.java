package com.internship.management.interfaces;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface ChartInterface {

    ByteArrayInputStream exportInternshipsByDepartment() throws IOException;
    List<DepartmentInternshipStat> getInternshipsByDepartment();

}
