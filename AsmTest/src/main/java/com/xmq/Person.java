package com.xmq;

public class Person {
    public String mName;
    public String mEnName;
    public int mAge;
    public void print() {
        String info = "dd";
        Log.d("Person", "print==="+info);
        System.out.println("print() invoke");
    }

    public void print(Exception ex) {
        String info = "dd";
        Log.d("Person", "print==="+info ,ex);
        print();
        System.out.println("print() invoke");
    }
    public StringBuffer print(StringBuffer buffer) {
        buffer.append("Name: ");
        buffer.append(mName);
        buffer.append("EnName: ");
        buffer.append(mEnName);
        System.out.println("print: ");
        Log.d("Person", "print===");
        return buffer;
    }

    @Override
    public String toString() {
        String info = "dd";
        Log.d("Person", "toString==="+info, new IllegalArgumentException(""));
        return "Person{" +
                "mName='" + mName + '\'' +
                ", mEnName='" + mEnName + '\'' +
                ", mAge=" + mAge +
                '}';
    }

}
