
CREATE TABLE IntranetOrpecCredit.dbo.Cache (
	hash varchar(65) NOT NULL,
	[data] datetime NOT NULL,
	resposta varchar NOT NULL,
	protocolo varchar(25) NOT NULL,
	CONSTRAINT PK__Cache__2E3BD7D3 PRIMARY KEY (hash)
)

CREATE INDEX index_hach ON IntranetOrpecCredit.dbo.Cache (hash)



