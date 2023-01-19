package com.example.network

import com.example.common.models.Result
import com.example.models.Branch
import com.example.models.Client
import com.example.models.Company
import com.example.models.auth.Credentials
import com.example.models.auth.Register
import com.example.models.auth.Token
import com.example.models.utils.Address
import com.example.models.utils.BusinessType
import com.example.models.utils.TaxStatus

class FakeKtorRemoteDataSource : EInvoiceRemoteDataSource {
    override suspend fun login(credentials: Credentials): Result<Token> {
        return Result.Success(Token("token"))
    }

    override suspend fun register(register: Register): Result<Token> {
        return Result.Success(Token("token"))
    }

    override suspend fun logout(): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun createCompany(company: Company): Result<Company> {
        return Result.Success(company)
    }

    override suspend fun getCompany(companyId: String): Result<Company> {
        return Result.Success(Company("company", "123", "me", "phone", null))
    }

    override suspend fun getCompanies(): Result<List<Company>> {
        return Result.Success(List(10) { Company("company", "123", "me", "phone", null) })
    }

    override suspend fun updateCompany(company: Company): Result<Company> {
        return Result.Success(company)
    }

    override suspend fun deleteCompany(companyId: String): Result<Company> {
        return Result.Success(Company("company", "123", "me", "phone", null, companyId))
    }

    override suspend fun createClient(client: Client): Result<Client> {
        return Result.Success(client)
    }

    override suspend fun getClient(clientId: String): Result<Client> {
        return Result.Success(
            Client(
                id = clientId,
                registrationNumber = "123",
                name = "client",
                email = "email",
                phone = "phone",
                address = Address(
                    "street",
                    "country",
                    "zip",
                    "state",
                    "number",
                    "complement",
                    "district",
                    "reference",
                    "latitude",
                    "longitude",
                ),
                businessType = BusinessType.B,
                taxStatus = TaxStatus.Taxable,
                companyId = "123"
            )
        )
    }

    override suspend fun getClients(): Result<List<Client>> {
        return Result.Success(
            List(10) {
                Client(
                    id = "123",
                    registrationNumber = "123",
                    name = "client",
                    email = "email",
                    phone = "phone",
                    address = Address(
                        "street",
                        "city",
                        "country",
                        "zip",
                        "state",
                        "number",
                        "complement",
                        "district",
                        "reference",
                        "latitude",
                    ),
                    businessType = BusinessType.B,
                    taxStatus = TaxStatus.Taxable,
                    companyId = "123"
                )
            }
        )
    }

    override suspend fun updateClient(client: Client): Result<Client> {
        return Result.Success(client)
    }

    override suspend fun deleteClient(clientId: String): Result<Client> {
        return Result.Success(
            Client(
                id = clientId,
                registrationNumber = "123",
                name = "client",
                email = "email",
                phone = "phone",
                address = Address(
                    "street",
                    "city",
                    "country",
                    "zip",
                    "state",
                    "number",
                    "complement",
                    "district",
                    "reference",
                    "latitude",
                ),
                businessType = BusinessType.B,
                taxStatus = TaxStatus.Taxable,
                companyId = "123"
            )
        )
    }

    override suspend fun createBranch(branch: Branch): Result<Branch> {
        return Result.Success(branch)
    }

    override suspend fun getBranch(branchId: String): Result<Branch> {
        return Result.Success(
            Branch(
                id = branchId,
                name = "branch",
                internalId = "1",
                companyId = "street",
                street = "city",
                country = "zip",
                governate = "state",
                postalCode = "number",
                regionCity = "complement",
                buildingNumber = "district",
                floor = "reference",
                room = "latitude",
                landmark = "123",
                additionalInformation = "longitude"
            )
        )
    }

    override suspend fun getBranches(): Result<List<Branch>> {
        return Result.Success(
            List(10) {
                Branch(
                    id = "123",
                    name = "branch",
                    internalId = "1",
                    companyId = "street",
                    street = "city",
                    country = "zip",
                    governate = "state",
                    postalCode = "number",
                    regionCity = "complement",
                    buildingNumber = "district",
                    floor = "reference",
                    room = "latitude",
                    landmark = "123",
                    additionalInformation = "longitude"
                )
            }
        )
    }

    override suspend fun updateBranch(branch: Branch): Result<Branch> {
        return Result.Success(branch)
    }

    override suspend fun deleteBranch(branchId: String): Result<Branch> {
        return Result.Success(
            Branch(
                id = branchId,
                name = "branch",
                internalId = "1",
                companyId = "street",
                street = "city",
                country = "zip",
                governate = "state",
                postalCode = "number",
                regionCity = "complement",
                buildingNumber = "district",
                floor = "reference",
                room = "latitude",
                landmark = "123",
                additionalInformation = "longitude"
            )
        )
    }
}