
import play.api.mvc.WithFilters
import play.api.Application

object Global extends WithFilters(filters.CorsFilter)
