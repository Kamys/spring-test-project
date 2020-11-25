package ru.my.test.controller

import org.springframework.web.bind.annotation.*
import ru.my.test.model.ContactAddRequest
import ru.my.test.model.ContactEditRequest
import ru.my.test.model.ContactView
import ru.my.test.service.ContactService
import javax.validation.Valid

@RestController
@RequestMapping("/contacts")
class ContactsController(
    private var contactService: ContactService
) {

    @GetMapping
    fun getContacts(): List<ContactView> {
        return contactService.getAll()
    }

    @GetMapping("/{contactId}")
    fun getContacts(@PathVariable("contactId") contactId: Int): Any {
        return contactService.getById(contactId)
    }

    @PostMapping
    fun createContact(@Valid @RequestBody request: ContactAddRequest): ContactView {
        return contactService.add(request)
    }

    @PutMapping("/{contactId}")
    fun editContact(
        @PathVariable("contactId") contactId: Int,
        @RequestBody @Valid request: ContactEditRequest
    ): ContactView {
        return contactService.edit(contactId, request)
    }

    @DeleteMapping("/{contactId}")
    fun deleteContact(
        @PathVariable("contactId") contactId: Int
    ) {
        contactService.delete(contactId)
    }
}