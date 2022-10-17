package com.oocl.frm.demo.servlet;

import com.oocl.frm.transaction.FWJPAReadTransactionBlock;
import com.oocl.frm.transaction.exception.FWJPATransactionReadException;

import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

@WebServlet(name="DBCheckingServlet",urlPatterns="/dbchecking")
public class DBCheckingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            FWJPAReadTransactionBlock<Integer> txnReadBlock = new FWJPAReadTransactionBlock<Integer>("TestDBConnection.testDBConnectionIsActive.read"){
                @Override
                protected Integer run() throws FWJPATransactionReadException
                {
                    Integer sum = 0;

                    String checkSQL = "SELECT 1 + 1 FROM DUAL";

                    Query checkQuery = this.getEntityManager().createNativeQuery(checkSQL);

                    sum = ((BigDecimal)checkQuery.getSingleResult()).intValue();

                    return sum;
                }

            };

            Integer result = txnReadBlock.executeWithResult();

            // remove the threadlocal result holder so that memory could be released after Thread destroyed.
            txnReadBlock.removeResult();

            PrintWriter pw = resp.getWriter();
            pw.append("<HTML>");
            pw.append("<H3>");
            pw.append("Result of SELECT 1 + 1 FROM DUAL is : " + result);
            pw.append("</H3>");
            pw.append("</HTML>");

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServletException(ex);
        }

    }
}
