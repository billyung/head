package org.mifos.application.reports.business.service;

import static org.mifos.framework.util.helpers.NumberUtils.convertShortToInteger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.reports.business.ReportsJasperMap;
import org.mifos.application.reports.persistence.ReportsPersistence;
import org.mifos.application.reports.ui.SelectionItem;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.DatabaseSetup;

import servletunit.HttpServletRequestSimulator;
import servletunit.ServletContextSimulator;

public class ReportsBusinessServiceTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		DatabaseSetup.initializeHibernate();
	}

	public void testNothing() throws Exception {
	}

	/*
	 * This is disabled because some (not all) people are getting
	 * the exception:
	 * IOException: CreateProcess javac
	 * 
	 * Maybe javac needs to be in some kind of path, for Jasper
	 * reports to find it?
	 */
	public void xtestPdf() throws Exception {
		ServletContext context = new ServletContextSimulator();
		HttpServletRequestSimulator request = new HttpServletRequestSimulator(
				context);
		request.getSession().setAttribute("listOfAllParametersForReportId",
				Collections.EMPTY_LIST);

		ReportsJasperMap jasperMap = new ReportsJasperMap(null,
				"listgroups.jrxml");
		new ReportsPersistence().createJasperMap(jasperMap);
		short listGroups = jasperMap.getReportId();

		ReportsJasperMap reRead = new ReportsPersistence()
				.oneJasperOfReportId(listGroups);
		assertEquals("listgroups.jrxml", reRead.getReportJasper());

		new ReportsBusinessService().runReport(listGroups, request,
				"src/org/mifos/application/reports/uploads/",
				ReportsBusinessService.PDF);
	}
}
