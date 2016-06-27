package com.ibm.xmq.events;

import java.io.*;
import java.util.ArrayList;

public class XWriter
{

    public XWriter()
    {
        lines = new ArrayList(50);
        linePtr = -1;
        lineBuffer = new StringBuffer(80);
        printToError = false;
        printToFile = false;
        printToOut = true;
    }

    public void clearAllLines()
    {
        lines.clear();
        linePtr = -1;
        lineBuffer.delete(0, lineBuffer.length());
    }

    public void flushAllLines()
    {
        for(int i = 0; i < lines.size(); i++)
        {
            if(printToOut)
                System.out.print(lines.get(i));
            if(printToError)
                System.err.print(lines.get(i));
            if(printToFile)
                ps.print(lines.get(i));
        }

        clearAllLines();
    }

    public void flushLine(int lineNumber)
    {
        if(--lineNumber >= 0 && lineNumber < lines.size())
        {
            if(printToOut)
                System.out.print(lines.get(lineNumber));
            if(printToError)
                System.err.print(lines.get(lineNumber));
            if(printToFile)
                ps.print(lines.get(lineNumber));
        }
    }

    public String getLine(int lineNumber)
    {
        return (String)lines.get(lineNumber - 1);
    }

    public int getTotalLines()
    {
        return lines.size();
    }

    public void print(String line)
    {
        if(lineBuffer.length() == 0)
        {
            lines.add("");
            linePtr++;
        }
        lineBuffer.append(line);
        lines.set(linePtr, lineBuffer.toString());
    }

    public void println()
    {
        println("");
    }

    public void println(Object line)
    {
        if(lineBuffer.length() == 0)
        {
            lines.add("");
            linePtr++;
        }
        lineBuffer.append(line + System.getProperty("line.separator"));
        lines.set(linePtr, lineBuffer.toString());
        lineBuffer.delete(0, lineBuffer.length());
    }

    public boolean searchAllLines(String search)
    {
        boolean match = false;
        for(int i = 0; i < lines.size(); i++)
        {
            if(((String)lines.get(i)).indexOf(search) == -1)
                continue;
            match = true;
            break;
        }

        return match;
    }

    public void setPrintToError(boolean printToError)
    {
        this.printToError = true;
    }

    public void setPrintToFile(String fileName, boolean append)
        throws FileNotFoundException
    {
        ps = new PrintStream(new FileOutputStream(fileName, append));
        printToFile = true;
    }

    public void setPrintToOut(boolean printToOut)
    {
        this.printToOut = true;
    }

    private ArrayList lines;
    private int linePtr;
    private StringBuffer lineBuffer;
    private PrintStream ps;
    private boolean printToOut;
    private boolean printToError;
    private boolean printToFile;
}