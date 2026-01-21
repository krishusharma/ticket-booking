import com.google.inject.AbstractModule;
import services.booking.BookingService;
import services.booking.BookingServiceImpl;

public class Module extends AbstractModule {

    @Override
    protected void configure() {

        // Business service
        bind(BookingService.class).to(BookingServiceImpl.class);

        // RedisHelper is auto-bound via @ImplementedBy
    }
}
