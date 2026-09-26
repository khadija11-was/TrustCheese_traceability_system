package ma.trustCheese.TrustCheesebackend.service;

import lombok.RequiredArgsConstructor;
import ma.trustCheese.TrustCheesebackend.entity.Client;
import ma.trustCheese.TrustCheesebackend.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

	private final ClientRepository clientRepository;

	public List<Client> getActiveClients() {
		return clientRepository.findAll()
				.stream()
				.filter(client -> Boolean.TRUE.equals(client.getActif()))
				.toList();
	}
}
