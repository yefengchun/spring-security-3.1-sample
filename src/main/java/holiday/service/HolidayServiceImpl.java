package holiday.service;

import holiday.domain.HolidayRequest;
import holiday.security.UserContextService;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HolidayServiceImpl implements HolidayService {

	@PersistenceContext
	private transient EntityManager entityManager;
	
	@Autowired
	private UserContextService userContextService;


	@Transactional
	@PreAuthorize("hasRole('RIGHT_CREATE')")
	public void create(HolidayRequest hr) {
		hr.setEmployee(userContextService.getCurrentUser());
		this.entityManager.persist(hr);
	}


	@Transactional
	@PreAuthorize("hasRole('RIGHT_CANCEL') and " +
			"hasPermission(#hr, 'cancel')")
	public void cancel(HolidayRequest hr) {
		if (this.entityManager.contains(hr)) {
			this.entityManager.remove(hr);
		} else {
			HolidayRequest attached = this.entityManager.find(
					HolidayRequest.class, hr.getId());
			this.entityManager.remove(attached);
		}
	}

	@Transactional
	@PreAuthorize("hasRole('RIGHT_UPDATE')")
	public HolidayRequest update(HolidayRequest hr) {
		HolidayRequest merged = this.entityManager.merge(hr);
		this.entityManager.flush();
		return merged;
	}


	public long countHolidayRequests() {
		return entityManager.createQuery(
				"select count(o) from HolidayRequest o", Long.class)
				.getSingleResult();
	}

	@PreAuthorize("hasRole('RIGHT_CREATE')")
	@PostFilter("hasPermission(filterObject, 'show')")
	public List<HolidayRequest> findAllHolidayRequests() {
		return entityManager.createQuery("select o from HolidayRequest o",
				HolidayRequest.class).getResultList();
	}


	@PostAuthorize("hasPermission(returnObject, 'show')")
	public HolidayRequest findHolidayRequest(Long id) {
		if (id == null)
			return null;
		return entityManager.find(HolidayRequest.class, id);
	}

	public List<HolidayRequest> findHolidayRequestEntries(int firstResult,
			int maxResults) {
		return entityManager
				.createQuery("select o from HolidayRequest o",
						HolidayRequest.class).setFirstResult(firstResult)
				.setMaxResults(maxResults).getResultList();
	}

}
