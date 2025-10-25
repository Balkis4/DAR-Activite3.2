package serverPackage;

import java.io.Serializable;

public class Operation implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private double op1;
    private double op2;
    private char opr;
    private double r;
    private int oprId;

    public Operation(double op1, double op2, char opr) {
        this.op1 = op1;
        this.op2 = op2;
        this.opr = opr;
    }

    // Getters et Setters
    public double getOp1() {
    	return op1; }
    public void setOp1(double op1) { 
    	this.op1 = op1; }

    public double getOp2() { 
    	return op2; }
    public void setOp2(double op2) { 
    	this.op2 = op2; }

    public char getOpr() { 
    	return opr; }
    public void setOpr(char opr) { 
    	this.opr = opr; }

    public double getR() { 
    	return r; }
    public void setR(double r) { 
    	this.r = r; }

    public int getOprId() { 
    	return oprId; }
    public void setOprId(int oprId) { this.oprId = oprId; }

    @Override
    public String toString() {
        return op1 + " " + opr + " " + op2 + " = " + r + " (ID: " + oprId + ")";
    }
}