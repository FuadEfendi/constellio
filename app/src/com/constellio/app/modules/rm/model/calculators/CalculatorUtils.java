package com.constellio.app.modules.rm.model.calculators;

import static org.joda.time.Days.daysBetween;

import java.util.List;

import org.joda.time.LocalDate;

import com.constellio.app.modules.rm.model.CopyRetentionRule;

public class CalculatorUtils {

	public static LocalDate toNextEndOfYearDateIfNotAlready(LocalDate date, String yearEndStr, int requiredDaysBeforeYearEnd) {
		if (isEndOfYear(date, yearEndStr)) {
			return date;
		} else {
			return toNextEndOfYearDate(date, yearEndStr, requiredDaysBeforeYearEnd);
		}
	}

	public static LocalDate toEndOfYear(int year, String yearEndStr) {
		int indexOfSep = yearEndStr.indexOf("/");
		int yearEndMonth = Integer.parseInt(yearEndStr.substring(0, indexOfSep));
		int yearEndDay = Integer.parseInt(yearEndStr.substring(indexOfSep + 1));
		return new LocalDate(year, yearEndMonth, yearEndDay);
	}

	//FIXME make it private?
	static LocalDate toNextEndOfYearDate(LocalDate date, String yearEndStr, int requiredDaysBeforeYearEnd) {
		if (date == null) {
			return null;
		}
		LocalDate yearEndDate;

		yearEndDate = toEndOfYear(date.getYear(), yearEndStr);

		if (yearEndDate.isBefore(date)) {
			yearEndDate = yearEndDate.plusYears(1);
		}

		int daysBetweenDateAndYearEndDate = daysBetween(date, yearEndDate).getDays();

		if (daysBetweenDateAndYearEndDate < requiredDaysBeforeYearEnd) {
			yearEndDate = yearEndDate.plusYears(1);
		}
		return yearEndDate;
	}

	public static LocalDate getSmallestDate(List<LocalDate> dates) {

		LocalDate smallest = null;
		for (LocalDate date : dates) {
			if (smallest == null || (date != null && date.isBefore(smallest))) {
				smallest = date;
			}
		}

		return smallest;
	}

	public static boolean isEndOfYear(LocalDate date, String yearEndStr) {
		if (date == null) {
			return false;
		}
		int indexOfSep = yearEndStr.indexOf("/");
		int yearEndMonth = Integer.parseInt(yearEndStr.substring(0, indexOfSep));
		int yearEndDay = Integer.parseInt(yearEndStr.substring(indexOfSep + 1));
		return date.getDayOfMonth() == yearEndDay && date.getMonthOfYear() == yearEndMonth;
	}

	public static LocalDate calculateExpectedTransferDate(CopyRetentionRule copyRule, LocalDate adjustedDecommissioningDate,
			int defaultNumberOfYearWhenVariableDelay) {

		int numberOfYearWhenVariableDelay = defaultNumberOfYearWhenVariableDelay;
		if (copyRule.getOpenActiveRetentionPeriod() != null && copyRule.getOpenActiveRetentionPeriod() >= 0) {
			numberOfYearWhenVariableDelay = copyRule.getOpenActiveRetentionPeriod();
		}

		if (adjustedDecommissioningDate == null) {
			return null;
		}

		if (copyRule.getActiveRetentionPeriod().isVariablePeriod()) {
			if (numberOfYearWhenVariableDelay == -1) {
				return null;
			} else {
				return adjustedDecommissioningDate.plusYears(numberOfYearWhenVariableDelay);
			}
		} else {
			return adjustedDecommissioningDate.plusYears(copyRule.getActiveRetentionPeriod().getFixedPeriod());
		}

	}

	public static LocalDate calculateExpectedInactiveDate(CopyRetentionRule copyRule,
			LocalDate baseTransferDate, int numberOfYearWhenVariableDelayPeriod) {

		if (baseTransferDate == null) {
			return null;
		} else if (copyRule.getSemiActiveRetentionPeriod().isVariablePeriod()) {
			if (numberOfYearWhenVariableDelayPeriod == -1) {
				return null;
			} else {
				return baseTransferDate.plusYears(numberOfYearWhenVariableDelayPeriod);
			}
		} else {
			return baseTransferDate.plusYears(copyRule.getSemiActiveRetentionPeriod().getFixedPeriod());
		}

	}
}
