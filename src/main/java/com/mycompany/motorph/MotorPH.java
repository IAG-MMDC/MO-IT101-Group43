/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.motorph;

/**
 * Isabelle Angeli Gallardo
 * Jam Rosales
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

public class MotorPH {
    
    static String empCsv = "resources/MotorPH_Employee Data - Employee Details.csv";
    static String attCsv = "resources/MotorPH_Employee Data - Attendance Record.csv";
    static String[] empNos = new String[34];
    static String[] empFirstNames = new String[34];
    static String[] empLastNames = new String[34];
    static String[] empBirthdays = new String[34];
    static double[] hourlyRates = new double[34];
    static int count = 0;
    static String option = "";  
    
    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);
        
        System.out.print("Enter username: ");
        String inputUser = scan.nextLine();
        
        System.out.print("Enter password: ");
        String inputPass = scan.nextLine();
       
        //Access verification 
        if (inputUser.equals("employee") && inputPass.equals("12345")) {

            System.out.println("\n1. Enter employee number.");
            System.out.println("2. Exit the program.");
            System.out.print("Choose an option: ");
            option = scan.nextLine();

            if (option.equals("1")) {

                System.out.print("\nEnter employee no.: ");
                String inputEmpNo = scan.nextLine();                   
                
                readEmpData();
                for (int i = 0; i < count; i++) {
                if(inputEmpNo.equals(empNos[i])) {
                    printEmpDetails(i);
                    return;
                } else {
                    System.out.println("Employee number does not exist.");
                    return;
                }
                } 
            } else {
                System.out.println("Exiting program...");
            }
        }else if (inputUser.equals("payroll_staff") && inputPass.equals("12345")) {
            
            System.out.println("\n1. Process Payroll."); 
            System.out.println("2. Exit the program.");
            System.out.print("Choose an option: ");
            option = scan.nextLine();
            
            if (option.equals("1")){
                
            System.out.println("\n1. One employee.");
            System.out.println("2. All employees.");
            System.out.println("3. Exit the program.");
            System.out.print("Choose an option: ");
            option = scan.nextLine();

                switch (option) {
                    case "1":
                            System.out.print("\nEnter employee no.: ");
                            String inputEmpNo = scan.nextLine();
                            
                            readEmpData();
                            for (int i = 0; i < count; i++) {
                                if(inputEmpNo.equals(empNos[i])) {
                                printEmpDetails(i);
                                readAttData(empNos[i], hourlyRates[i]);
                                return;
                                } else {
                                    System.out.println("Employee number does not exist.");
                                    return;
                                }
                            }
                        break;
                    case "2":
                        readEmpData();
                        for (int i = 0; i < count; i++) {
                            printEmpDetails(i);
                            readAttData(empNos[i], hourlyRates[i]);
                        }
                        break;
                    default: System.out.println("Exiting program...");
                }
            } else {
                System.out.println("Exiting program...");
            }
        } else if ((!inputUser.equals("payroll_staff") && !inputUser.equals("employee")) && !inputPass.equals("12345")) {
                System.out.print("Incorrect username and password.");
        } else if (!inputPass.equals("12345")) {
                System.out.println("Incorrect password.");
        } else {
                System.out.println("Incorrect username");
        }
    }
    //Reads employee data while assigning the value to its designated array
    public static void readEmpData() {
        
       try (BufferedReader br = new BufferedReader(new FileReader(empCsv))) { 

            br.readLine(); 
            String currentLine;

            while ((currentLine = br.readLine()) != null) {
                if (currentLine.trim().isEmpty()) continue;

                String[] data = currentLine.split(",");
                
                    empNos[count] = data[0];
                    empLastNames[count] = data[1];
                    empFirstNames[count] = data[2];
                    empBirthdays[count] = data[3];
                    hourlyRates[count] = Double.parseDouble(data[data.length-1]);
                    count++;
                }
            }
            catch (Exception e) {
            System.out.println("An error occurred while reading employee file.");
            e.printStackTrace();
            } 
       
        }
    //Prints employee details
    public static void printEmpDetails(int i){ 
        System.out.println("\n===================================");
        System.out.println("Employee # : " + empNos[i]);
        System.out.println("Employee Name : " + empLastNames[i] + ", " + empFirstNames[i]);
        System.out.println("Birthday : " + empBirthdays[i]);
        System.out.println("===================================");
   } 
   //Reads the attendance csv 
   //Loops the First Cut-off and Second Cut-off per month starting June until December
   //Prints payroll details
   public static void readAttData(String empNo, double hourlyRate) {
   
   for (int month = 6; month <= 12; month++) { 
            double firstCutoff = 0;
            double secondCutoff = 0;
            String monthName = getMonthName(month);
            String daysInMonth = getDaysInMonth(month);

            try (BufferedReader br = new BufferedReader(new FileReader(attCsv))) {

                br.readLine(); 
                String line;

                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;

                    String[] data = line.split(",");
                    
                    if (!data[0].equals(empNo)) continue;

                    String[] dateParts = data[3].split("/");
                    int attMonth = Integer.parseInt(dateParts[0]);
                    int attDay = Integer.parseInt(dateParts[1]);
                    int attYear = Integer.parseInt(dateParts[2]);
                    
                    if (attYear != 2024) continue; 
                    if (attMonth != month) continue;

                    String[] loginParts = data[4].trim().split(":");
                    int loginHour = Integer.parseInt(loginParts[0]);
                    int loginMin = Integer.parseInt(loginParts[1]);
                     
                    String[] logoutParts = data[5].trim().split(":");
                    int logoutHour = Integer.parseInt(logoutParts[0]);
                    int logoutMin = Integer.parseInt(logoutParts[1]);

                    double hours = computeHours(loginHour, loginMin, logoutHour,logoutMin);

                    if (attDay <= 15) {
                        firstCutoff += hours;
                    } else {
                        secondCutoff += hours;
                    }
                }

            } catch (Exception e) {
                System.out.println("An error occurred while reading attendance file.");
                e.printStackTrace(); 
            }      
            double firstGrossSalary = firstCutoff * hourlyRate;
            double grossMonthlySalary = (firstCutoff + secondCutoff) * hourlyRate;
            
            double sss = computeSSS(grossMonthlySalary);
            double pagIbig = computePagIbig(grossMonthlySalary);
            double philHealth = computePhilHealth(grossMonthlySalary);
            double totalDeductions = computeTotalDeductions(sss, pagIbig, philHealth);
            double taxableIncome = computeTaxableIncome(grossMonthlySalary, totalDeductions);
            double witholdingTax = computeWithholdingTax(taxableIncome);
            double netSalary = taxableIncome - witholdingTax;
            
            printPayrollDetails(monthName, firstCutoff, secondCutoff, daysInMonth, firstGrossSalary,
            grossMonthlySalary, totalDeductions, sss, philHealth, pagIbig,witholdingTax, netSalary);    
  
        }
   }
   //Returns month name depending on the month
    public static String getMonthName(int month) {
       
            switch (month) {
                case 6: return "June"; 
                case 7: return "July"; 
                case 8: return "August"; 
                case 9: return "September"; 
                case 10: return  "October"; 
                case 11: return "November";
                case 12: return "December"; 
                default: return  "";
            }
    }
    //Returns  number of days in month depending on the month
    public static String getDaysInMonth(int month) {
        
            switch (month) {
                case 6: return "30";
                case 7: return "31"; 
                case 8: return "31"; 
                case 9: return "30"; 
                case 10: return "31"; 
                case 11: return "30";
                case 12: return "31"; 
                default: return ""; 
            }
    }
    
    //Method for printing payroll details
    public static void printPayrollDetails(String monthName, double firstCutoff, double secondCutoff, String daysInMonth, double firstGrossSalary,
    double grossMonthlySalary, double totalDeductions, double sss, double philHealth, double pagIbig, double withholdingTax, double netSalary) {     
            System.out.println("\nCutoff Date: " + monthName + " 1 to 15");
            System.out.println("Total Hours Worked : " + firstCutoff);
            System.out.println("Gross Salary: " + firstGrossSalary); 
            System.out.println("Net Salary: " + firstGrossSalary); 

            System.out.println("\nCutoff Date: " + monthName + " 16 to " + daysInMonth);
            System.out.println("Total Hours Worked : " + secondCutoff);
            System.out.println("Gross Salary: " + grossMonthlySalary); 
            System.out.println("Deductions: " + totalDeductions);
            System.out.println("    SSS: " + sss);
            System.out.println("    PhilHealth: " + philHealth);
            System.out.println("    Pag-IBIG: " + pagIbig);
            System.out.println("    Tax: " + withholdingTax);
            System.out.println("Net Salary: "+ netSalary);
    }
    
    //Calculates the hours worked
    public static double computeHours(int loginHour, int loginMin, int logoutHour, int logoutMin) {
        int graceLimit = 8 * 60 + 10;
        int startTime = 8 * 60;
        int login = loginHour * 60 + loginMin;
        int logout = logoutHour * 60 + logoutMin;

        if (logout > 17 * 60) {
            logout = 17 * 60;
        }
        if (login <= graceLimit) {
            login = startTime;
        }
        
        int lateMinutes = Math.max(0, login - startTime);
        int minutesWorked = Math.min(logout - login, 540 - lateMinutes);
        
        minutesWorked -= 60;
        
        if (minutesWorked < 0) minutesWorked = 0;
        
        return minutesWorked / 60.0;
    }
    
    //Calculate SSS Contribution
    public static double computeSSS(double grossMonthlySalary) {

        if (grossMonthlySalary < 3250) {
            return 135.00;
        }
        if (grossMonthlySalary >= 24750) {
            return 1125.00;
        }

        double salary = 3250;
        double contribution = 135;

        for (salary = 3250; grossMonthlySalary >= salary; salary += 500) {
            contribution += 22.50;
        }
        return contribution;
    }
    
    //Calculate Pag-Ibig Contribution
    public static double computePagIbig(double grossMonthlySalary) { 
        if (grossMonthlySalary >= 1000 && grossMonthlySalary <= 1500) {
            return Math.min(grossMonthlySalary * .03, 100);
        } else if (grossMonthlySalary > 1500) {
            return Math.min(grossMonthlySalary * .04, 100);
        } else {
            return 0;
        }
    }

    //Calculate PhilHealth Contribution
    public static double computePhilHealth(double grossMonthlySalary) { 
        if (grossMonthlySalary <= 10_000) {
            return (150.00);
        } else if (grossMonthlySalary >= 60_000) {
            return (900.00);
        } else {
            return (grossMonthlySalary * 0.03) / 2;    
       }      
    }

    //Calculate Total Deductions
    public static double computeTotalDeductions(double sss, double philhealth, double pagibig) { 
        return sss + philhealth + pagibig;        
    }

    //Calculate Taxable Income
    public static double computeTaxableIncome(double grossMonthlySalary, double totalDeductions) { 
        return grossMonthlySalary - totalDeductions;
    }

    //Calculate Withholding Tax
    public static double computeWithholdingTax(double taxableIncome) { 
        if (taxableIncome < 20_833) {
            return 0;
        } else if (taxableIncome < 33_333) {
            return (taxableIncome - 20_833) * .20;
        } else if (taxableIncome < 66_667) {
            return (((taxableIncome - 33_333) * .25) + 2_500);
        } else if (taxableIncome < 166_667) {
            return (((taxableIncome - 66_667) * .30) + 10_833);
        } else if (taxableIncome < 666_667) {
            return (((taxableIncome - 166_667) * .32) + 40_833.33);
        } else {
            return (((taxableIncome - 666_667) * .35) + 200_833.33);
        }
    }
}

