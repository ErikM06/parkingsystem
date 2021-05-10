package com.parkit.parkingsystem.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;

public class ApplyFivePercentDiscountOnFare {

	private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

	private InputReaderUtil inputReaderUtil;
	private ParkingSpotDAO parkingSpotDAO;
	private TicketDAO ticketDAO;
	private Ticket ticket;
	private DataBaseConfig dataBaseConfig;

	public boolean checkRightForFivePercentDiscount() {
		Connection con = null;
		ResultSet rs = null;
		String readerRegNumber = null;
		String canGetTheDiscount = null;
		Boolean discount = null;
		
		try {
			con = dataBaseConfig.getConnection();
			readerRegNumber = ticket.getVehicleRegNumber();

			PreparedStatement ps = con.prepareStatement(
					"SELECT VEHICLE_REG_NUMBER FROM ticket WHERE VEHICLE_REG_NUMBER = ? AND OUT_TIME IS NOT NULL");
			ps.setString(1, readerRegNumber);
			rs = ps.executeQuery();
			if (rs.next()) {
				canGetTheDiscount = rs.getString(1);
			}
			if(canGetTheDiscount == readerRegNumber) {
				discount = true;
				System.out.println(
						"Welcome back!  As a recurring user of our parking lot, you'll benefit from a 5% discount.");
			} else { discount = false ;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return discount;

	}
	public void applyFivePercentDiscount(Ticket ticket) {
		double priceWithoutDiscount = 0;
		double priceWithDiscount = 0;
		
		if(checkRightForFivePercentDiscount()) {
			priceWithoutDiscount = ticket.getPrice();
			priceWithDiscount = priceWithoutDiscount *(5/100);
			ticket.setPrice(priceWithDiscount);
		}
		
		
	}
}
