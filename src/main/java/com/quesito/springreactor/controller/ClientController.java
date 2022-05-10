package com.quesito.springreactor.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.quesito.springreactor.model.Client;
import com.quesito.springreactor.pagination.PageSupport;
import com.quesito.springreactor.service.IClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private IClientService iClientService;

    @GetMapping
    public Mono<ResponseEntity<Flux<Client>>> findAll() {
        Flux<Client> clientFlux = iClientService.findAll();
        return Mono.just(ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(clientFlux));
    }


    @GetMapping("/{id}")
    public Mono<ResponseEntity<Client>> findById(@PathVariable("id") String id) {
        return iClientService.findById(id).map(client -> ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(client));
    }

    @PostMapping
    public Mono<ResponseEntity<Client>> save(@Valid @RequestBody Client client) {

        return iClientService.save(client).map(p -> ResponseEntity
//                .created(URI.create(request.getURI().toString().concat("/").concat(client.getId())))
                .created(URI.create(client.getId()))
                .body(p));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Client>> update(@PathVariable("id") String id, @RequestBody Client client) {
        Mono<Client> clientMono = Mono.just(client);
        Mono<Client> monoDb = iClientService.findById(id);
        return monoDb
                .zipWith(clientMono, (dbClient, bodyClient) -> {
                    dbClient.setFirstName(bodyClient.getFirstName());
                    dbClient.setBirthDate(bodyClient.getBirthDate());
                    dbClient.setLastName(bodyClient.getLastName());
                    dbClient.setUrlPhoto(bodyClient.getUrlPhoto());
                    return dbClient;
                })
                .flatMap(iClientService::update)
                .map(p -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") String id) {
        return iClientService.findById(id)
                .flatMap(client -> iClientService.deleteById(client.getId())
                        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/hateoas/{id}")
    public Mono<EntityModel<Client>> getHateoasById(@PathVariable("id") String id) {
        Mono<Link> link1 = Mono.just(
                linkTo(methodOn(ClientController.class).getHateoasById(id)).withSelfRel());
        Mono<Link> link2 = Mono.just(
                linkTo(methodOn(ClientController.class).getHateoasById(id)).withRel("client"));
        Mono<Link> link3 = Mono.just(
                linkTo(methodOn(ClientController.class).getHateoasById(id)).withRel("order"));
//        return iClientService
//                .findById(id)
//                .zipWith(link1,EntityModel::of);
        return link1
                .zipWith(link2)
                .map(tuple -> Links.of(tuple.getT1(), tuple.getT2()))
                .zipWith(iClientService.findById(id), (links, client) -> EntityModel.of(client, links));
    }

    @GetMapping("/pageable")
    public Mono<ResponseEntity<PageSupport<Client>>> findAllPageable(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        return iClientService.getPage(PageRequest.of(page, size))
                .map(p -> ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(ResponseEntity.noContent().build());

    }

    @PostMapping("/v1/upload/{id}")
    public Mono<ResponseEntity<Client>> uploadV1(@PathVariable("id") String id, @RequestPart("file") FilePart file) throws IOException {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils
                .asMap("cloud_name", "doyq4nlvu",
                        "api_key", "562769199496665",
                        "api_secret", "f6mP7Rf73MaYlbiK_TUvV21R2sA"));
        File f = Files.createTempFile("temp", file.filename()).toFile();
        return file.transferTo(f)
                .then(iClientService.findById(id))
                .flatMap(client -> {
                    try {
                        var response = cloudinary.uploader().upload(f, ObjectUtils.asMap("resource_type", "auto"));
                        client.setUrlPhoto(response.get("url").toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return iClientService.update(client);

                })
                .map(client -> ResponseEntity.ok().body(client))
                .defaultIfEmpty(ResponseEntity.notFound().build());


    }
}
