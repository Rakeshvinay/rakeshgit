package com.brihathi.Multi_Tenant.dto;
 
import java.util.List;
 
public class ScoreProgressResponseDTO {
    private String subject;
    private List<WeekStat> weeks;
    private Summary summary;
 
    public ScoreProgressResponseDTO(String subject, List<WeekStat> weeks, Summary summary) {
        this.subject = subject;
        this.weeks = weeks;
        this.summary = summary;
    }
 
    public String getSubject() {
        return subject;
    }
 
    public void setSubject(String subject) {
        this.subject = subject;
    }
 
    public List<WeekStat> getWeeks() {
        return weeks;
    }
 
    public void setWeeks(List<WeekStat> weeks) {
        this.weeks = weeks;
    }
 
    public Summary getSummary() {
        return summary;
    }
    public void setSummary(Summary summary) {
        this.summary = summary;
    }
 
    public static class WeekStat {
        private int week;
        private Double averagePercentage;
        private Double totalPercentage;
        private Integer countRows;
        private Double diffFromPreviousWeek;
 
        public WeekStat(int week, Double averagePercentage, Double totalPercentage, Integer countRows) {
            this.week = week;
            this.averagePercentage = averagePercentage;
            this.totalPercentage = totalPercentage;
            this.countRows = countRows;
            this.diffFromPreviousWeek = null;
        }
        public WeekStat(int week, Double averagePercentage, Double totalPercentage, Integer countRows, Double diffFromPreviousWeek) {
            this.week = week;
            this.averagePercentage = averagePercentage;
            this.totalPercentage = totalPercentage;
            this.countRows = countRows;
            this.diffFromPreviousWeek = diffFromPreviousWeek;
        }
 
        public int getWeek() {
            return week;
        }
 
        public void setWeek(int week) {
            this.week = week;
        }
 
        public Double getAveragePercentage() {
            return averagePercentage;
        }
 
        public void setAveragePercentage(Double averagePercentage) {
            this.averagePercentage = averagePercentage;
        }
 
        public Double getTotalPercentage() {
            return totalPercentage;
        }
 
        public void setTotalPercentage(Double totalPercentage) {
            this.totalPercentage = totalPercentage;
        }
 
        public Integer getCountRows() {
            return countRows;
        }
 
        public void setCountRows(Integer countRows) {
            this.countRows = countRows;
        }
 
        public Double getDiffFromPreviousWeek() { return diffFromPreviousWeek; }
        public void setDiffFromPreviousWeek(Double diffFromPreviousWeek) { this.diffFromPreviousWeek = diffFromPreviousWeek; }
    }
 
    public static class Summary {
        private Double averagePercentage;
        private Double totalPercentage;
        private Integer countRows;
 
        public Summary(Double averagePercentage, Double totalPercentage, Integer countRows) {
            this.averagePercentage = averagePercentage;
            this.totalPercentage = totalPercentage;
            this.countRows = countRows;
        }
        public Double getAveragePercentage() { return averagePercentage; }
        public void setAveragePercentage(Double averagePercentage) { this.averagePercentage = averagePercentage; }
        public Double getTotalPercentage() { return totalPercentage; }
        public void setTotalPercentage(Double totalPercentage) { this.totalPercentage = totalPercentage; }
        public Integer getCountRows() { return countRows; }
        public void setCountRows(Integer countRows) { this.countRows = countRows; }
    }
}
 