package net.curmudgeon.suds.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import net.curmudgeon.suds.SudsApplication;
import net.curmudgeon.suds.entity.Parent;
import net.curmudgeon.suds.entity.Pet;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SudsApplication.class)
@WebAppConfiguration
@ActiveProfiles("local")
@TestPropertySource(properties = { 
		"amazon.dynamodb.endpoint=http://localhost:8000/", 
		"amazon.aws.accesskey=accesskey1",
		"amazon.aws.secretkey=secretkey1" })
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class CustomerRepositoryIntegrationTest {

	@Autowired
	private DynamoDbEnhancedClient dynamoDbEnhancedClient;

	private DynamoDbTable<Parent> parentTable;
	private DynamoDbTable<Pet> petTable;
	
	private static final String PHONE1 = "4101231234";
	private static final String PHONE2 = "4439998888";

	@BeforeAll
	public void setup() throws Exception {
		parentTable = dynamoDbEnhancedClient.table("Customer", TableSchema.fromBean(Parent.class));
		
		try {
			parentTable.deleteTable();
		} catch (ResourceNotFoundException e) {
			// Do nothing, table doesn't exist.
		}
		
		parentTable.createTable();

		petTable = dynamoDbEnhancedClient.table("Customer", TableSchema.fromBean(Pet.class));
	}
	
	@Test
	public void a_testParentSavesAndRetrieves() throws Exception {
		Map<String,String> address = new HashMap<String,String>();
		address.put("Street", "123 Main Street");
		address.put("City", "Baltimore");
		address.put("State", "MD");
		address.put("ZipCode", "21213");

		Parent parent = new Parent();
		parent.setFirstName("Joan");
		parent.setLastName("Doe");
		parent.setPhoneNumber(PHONE1);
		parent.setAddress(address);
		
		parent.setCustomerId("CUSTOMER#"+PHONE1);
		parent.setId("PARENT#"+parent.getFirstName()+parent.getLastName());
		
		parentTable.putItem(parent);
		
		Map<String,String> anotherAddress = new HashMap<String,String>();
		anotherAddress.put("Street", "123 Main Street");
		anotherAddress.put("City", "Crofton");
		anotherAddress.put("State", "MD");
		anotherAddress.put("ZipCode", "21114");

		Parent anotherParent = new Parent();
		anotherParent.setFirstName("John");
		anotherParent.setLastName("Smith");
		anotherParent.setPhoneNumber(PHONE2);
		anotherParent.setAddress(anotherAddress);
		
		anotherParent.setCustomerId("CUSTOMER#"+PHONE2);
		anotherParent.setId("PARENT#"+anotherParent.getFirstName()+anotherParent.getLastName());
		
		parentTable.putItem(anotherParent);

		Key key = Key.builder()
                .partitionValue("CUSTOMER#"+PHONE1)
                .sortValue("PARENT#"+parent.getFirstName()+parent.getLastName())
                .build();
		
		Parent result = parentTable.getItem(key);
		
        assertNotNull(result);
        assertEquals(result.getAddress().size(), 4, ()->"address size of " + result.getAddress().size() + " does not equal 4");
        assertEquals(result.getFirstName(), parent.getFirstName(), ()->"first name " + result.getFirstName() + " is not " + parent.getFirstName());
        assertEquals(result.getLastName(), parent.getLastName(), ()->"last name " + result.getLastName() + " is not " + parent.getLastName());
        assertEquals(result.getPhoneNumber(), parent.getPhoneNumber(), ()->"phone number " + result.getPhoneNumber() + " is not " + parent.getPhoneNumber());

		Key anotherKey = Key.builder()
                .partitionValue("CUSTOMER#"+PHONE2)
                .sortValue("PARENT#"+anotherParent.getFirstName()+anotherParent.getLastName())
                .build();
		
		Parent anotherResult = parentTable.getItem(anotherKey);
		
        assertNotNull(anotherResult);
        assertEquals(anotherResult.getAddress().size(), 4, ()->"address size of " + anotherResult.getAddress().size() + " does not equal 4");
        assertEquals(anotherResult.getFirstName(), anotherParent.getFirstName(), ()->"first name " + anotherResult.getFirstName() + " is not " + anotherParent.getFirstName());
        assertEquals(anotherResult.getLastName(), anotherParent.getLastName(), ()->"last name " + anotherResult.getLastName() + " is not " + anotherParent.getLastName());
        assertEquals(anotherResult.getPhoneNumber(), anotherParent.getPhoneNumber(), ()->"phone number " + anotherResult.getPhoneNumber() + " is not " + anotherParent.getPhoneNumber());
	}
	
	@Test
	public void b_testPetSavesAndRetrieves() throws Exception {
		Pet pet = new Pet();
		pet.setName("Buddy");
		pet.setType("Dog");
		pet.setNotes("A little bitey in the harness.");
		
		pet.setCustomerId("CUSTOMER#"+PHONE1);
		pet.setId("PET#"+pet.getName());
		
		petTable.putItem(pet);
		
		Pet anotherPet = new Pet();
		anotherPet.setName("Fluffernutter");
		anotherPet.setType("Dog");
		anotherPet.setNotes("Likes peanut butter treats.");
		
		anotherPet.setCustomerId("CUSTOMER#"+PHONE1);
		anotherPet.setId("PET#"+anotherPet.getName());
		
		petTable.putItem(anotherPet);

		Pet yetAnotherPet = new Pet();
		yetAnotherPet.setName("Sparky");
		yetAnotherPet.setType("Cat");
		yetAnotherPet.setNotes("So mean, why are we washing cats?");
		
		yetAnotherPet.setCustomerId("CUSTOMER#"+PHONE2);
		yetAnotherPet.setId("PET#"+yetAnotherPet.getName());
		
		petTable.putItem(yetAnotherPet);

		Key key = Key.builder()
                .partitionValue("CUSTOMER#"+PHONE1)
                .sortValue("PET#"+pet.getName())
                .build();
		
		Pet result = petTable.getItem(key);
		
        assertNotNull(result);
        assertEquals(result.getName(), pet.getName(), ()->"name of " + result.getName() + " does not equal " + pet.getName());
        assertEquals(result.getType(), pet.getType(), ()->"type of " + result.getType() + " does not equal " + pet.getType());
        assertEquals(result.getNotes(), pet.getNotes(), ()->"name of " + result.getNotes() + " does not equal " + pet.getNotes());

		Key anotherKey = Key.builder()
                .partitionValue("CUSTOMER#"+PHONE1)
                .sortValue("PET#"+anotherPet.getName())
                .build();
		
		Pet anotherResult = petTable.getItem(anotherKey);
		
        assertNotNull(anotherResult);
        assertEquals(anotherResult.getName(), anotherPet.getName(), ()->"name of " + anotherResult.getName() + " does not equal " + anotherPet.getName());
        assertEquals(anotherResult.getType(), anotherPet.getType(), ()->"type of " + anotherResult.getType() + " does not equal " + anotherPet.getType());
        assertEquals(anotherResult.getNotes(), anotherPet.getNotes(), ()->"name of " + anotherResult.getNotes() + " does not equal " + anotherPet.getNotes());

		Key yetAnotherKey = Key.builder()
                .partitionValue("CUSTOMER#"+PHONE2)
                .sortValue("PET#"+yetAnotherPet.getName())
                .build();
		
		Pet yetAnotherResult = petTable.getItem(yetAnotherKey);
		
        assertNotNull(yetAnotherResult);
        assertEquals(yetAnotherResult.getName(), yetAnotherPet.getName(), ()->"name of " + yetAnotherResult.getName() + " does not equal " + yetAnotherPet.getName());
        assertEquals(yetAnotherResult.getType(), yetAnotherPet.getType(), ()->"type of " + yetAnotherResult.getType() + " does not equal " + yetAnotherPet.getType());
        assertEquals(yetAnotherResult.getNotes(), yetAnotherPet.getNotes(), ()->"name of " + yetAnotherResult.getNotes() + " does not equal " + yetAnotherPet.getNotes());
	}
	
	@Test
	public void c_testQueriesBasedOnPreviousTests() {
		
		// Retrieve pets for a specific customer. 
		Key petSortKey = Key.builder()
				.partitionValue("CUSTOMER#"+PHONE1)
                .sortValue("PET")
                .build();
		QueryConditional petQuery = QueryConditional.sortBeginsWith(petSortKey);
		PageIterable<Pet> petResults = petTable.query(petQuery);
		System.out.println("Pets for CUSTOMER#"+PHONE1);
		petResults.items().stream().forEach(item -> System.out.println(item));
		
		// Get just parents.
		AttributeValue attr = AttributeValue.builder().s("PARENT").build();
		Map<String,AttributeValue> values = new HashMap<>();
		values.put(":parentIdStart", attr);
		
		Map<String,String> names = new HashMap<>();
		names.put("#id", "id");
		
		Expression parentExpression = Expression.builder()
				.expressionValues(values)
				.expressionNames(names)
				.expression("begins_with(#id,:parentIdStart)")
				.build();
		
		ScanEnhancedRequest parentRequest = ScanEnhancedRequest.builder().filterExpression(parentExpression).build();
		PageIterable<Parent> parentResults = parentTable.scan(parentRequest);
		System.out.println("Just parents");
		parentResults.items().stream().forEach(item -> System.out.println(item));
	}
}
