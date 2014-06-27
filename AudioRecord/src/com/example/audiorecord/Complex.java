package com.example.audiorecord;

public class Complex {
	public double real;
	public double image;
	
	public Complex() {
		// TODO Auto-generated constructor stub
		this.real = 0;
		this.image = 0;
	}

	public Complex(double real, double image){
		this.real = real;
		this.image = image;
	}
	
	public Complex(int real, int image) {
		Integer integer = real;
		this.real = integer.floatValue();
		integer = image;
		this.image = integer.floatValue();
	}
	
	public Complex(double real) {
		this.real = real;
		this.image = 0;
	}
	
	public Complex cc(Complex complex) {
		Complex tmpComplex = new Complex();
		tmpComplex.real = this.real * complex.real - this.image * complex.image;
		tmpComplex.image = this.real * complex.image + this.image * complex.real;
		return tmpComplex;
	}
	
	public Complex sum(Complex complex) {
		Complex tmpComplex = new Complex();
		tmpComplex.real = this.real + complex.real;
		tmpComplex.image = this.image + complex.image;
		return tmpComplex;
	}
	
	public Complex cut(Complex complex) {
		Complex tmpComplex = new Complex();
		tmpComplex.real = this.real - complex.real;
		tmpComplex.image = this.image - complex.image;
		return tmpComplex;
	}
	
	public int getIntValue(){
		int ret = 0;
		ret = (int) Math.round(Math.sqrt(this.real*this.real - this.image*this.image));
		return ret;
	}
}
