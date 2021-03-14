package ch.puzzle.devtre.kafka.binance.boundary;

import ch.puzzle.devtre.kafka.binance.control.BinanceService;
import com.binance.api.client.domain.market.TickerPrice;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/prices")
public class BinanceResource {

    @Inject
    BinanceService service;

    @GET
    @Path("/{symbol}")
    @Produces(MediaType.APPLICATION_JSON)
    public TickerPrice list(@PathParam("symbol") String symbol) {
        return service.getPrice(symbol);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<TickerPrice> list() {
        return service.getPrices();
    }
}