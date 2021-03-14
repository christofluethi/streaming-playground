package ch.puzzle.devtre.kafka.binance.control;

import ch.puzzle.devtre.kafka.binance.boundary.BinanceKafkaProducer;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.TickerPrice;
import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class BinanceService {

    private static final Logger logger = LoggerFactory.getLogger(BinanceKafkaProducer.class.getName());

    @Inject
    BinanceKafkaProducer producer;

    @ConfigProperty(name = "binance.api.key")
    String apiKey;

    @ConfigProperty(name = "binance.api.secret")
    String apiSecret;

    @ConfigProperty(name = "binance.api.base-currency")
    String baseCurrency;

    private BinanceApiRestClient client;

    @PostConstruct
    public void init() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(apiKey, apiSecret);
        this.client = factory.newRestClient();
    }

    @Scheduled(concurrentExecution = Scheduled.ConcurrentExecution.SKIP, every = "2m")
    public void update() {
        List<TickerPrice> prices = getPrices();
        logger.info("Fetched {} prices", prices.size());
        for (TickerPrice price : prices) {
            producer.sendMessage(price.getSymbol(), price.getPrice());
        }
    }

    public TickerPrice getPrice(String symbol) {
        return client.getPrice(symbol.toUpperCase()+baseCurrency);
    }

    public List<TickerPrice> getPrices() {
        List<TickerPrice> allPrices = client.getAllPrices();
        return allPrices.stream().filter(t -> t.getSymbol().endsWith(baseCurrency)).collect(Collectors.toList());
    }
}
