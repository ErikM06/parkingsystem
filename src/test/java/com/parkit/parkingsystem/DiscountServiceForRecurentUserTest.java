package com.parkit.parkingsystem;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.DiscountServiceForRecurentUser;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@ExtendWith(MockitoExtension.class)
public class DiscountServiceForRecurentUserTest {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;
	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		new DiscountServiceForRecurentUser();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;

	}

	@BeforeEach
	private void prepareDB() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCD");
		dataBasePrepareService = new DataBasePrepareService();
		dataBasePrepareService.clearDataBaseEntries();
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		parkingService.processExitingVehicle();

	}

	@AfterAll
	private static void tearDown() {

	}

	@Test
	public void checkRightsForFivePercentDiscountTest() {
		Connection con = null;
		ResultSet rs = null;
		String readerRegNumber;
		String canGetTheDiscount = null;

		try {
			con = dataBaseTestConfig.getConnection();

			ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
			DiscountServiceForRecurentUser discountService = new  DiscountServiceForRecurentUser();
			Ticket ticket = new Ticket();
			parkingService.processIncomingVehicle();
			
			readerRegNumber = inputReaderUtil.readVehicleRegistrationNumber();
			
			
			PreparedStatement ps = con.prepareStatement(
					"SELECT VEHICLE_REG_NUMBER FROM ticket WHERE VEHICLE_REG_NUMBER = ? AND OUT_TIME IS NOT NULL");
			ps.setString(1, readerRegNumber);
			rs = ps.executeQuery();
			if (rs.next()) {
				canGetTheDiscount = rs.getString(1);
				ticket.setVehicleRegNumber(canGetTheDiscount);
			}
			discountService.checkRightForFivePercentDiscount(ticket);
			String ticketReg = ticket.getVehicleRegNumber();
			assertTrue(ticketReg.equals(readerRegNumber));
		

		} catch (SQLException e) {

		} catch (Exception e) {

		}

	}

	@Test
	public void applyFivePercentDiscountTest() {
		Connection con = null;
		double priceWithoutDiscount = 0;
		double priceWithDiscount = 0;
		String regNumber = null;
		int discount = 1;
		try {
			con = dataBaseTestConfig.getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE test.ticket SET DISCOUNT = ?, OUT_TIME = ? WHERE ID=1");
			
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			Ticket ticket = new Ticket();
			ps.setInt(1, discount);
			ps.setTimestamp(2, timestamp);
			DiscountServiceForRecurentUser discountServiceForRecurrentUser = new DiscountServiceForRecurentUser();
			ParkingService _parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		
			regNumber = inputReaderUtil.readVehicleRegistrationNumber();
			ps.executeUpdate();
			
			ticket = ticketDAO.getTicket(regNumber);
			priceWithoutDiscount = ticket.getPrice();
			discountServiceForRecurrentUser.applyFivePercentDiscount(ticket);
			priceWithDiscount = ticket.getPrice();
		
			assertTrue(priceWithDiscount == priceWithoutDiscount-(priceWithoutDiscount*0.05));

			

		} catch (SQLException e) {

		} catch (Exception e) {

		}
	}

}
