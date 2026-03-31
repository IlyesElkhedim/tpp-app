-- Insert default contract types (only if they don't already exist)
INSERT INTO contract_type (label)
SELECT 'Apprentissage'
WHERE NOT EXISTS (SELECT 1 FROM contract_type WHERE label = 'Apprentissage');

INSERT INTO contract_type (label)
SELECT 'Professionalisation'
WHERE NOT EXISTS (SELECT 1 FROM contract_type WHERE label = 'Professionalisation');

INSERT INTO contract_type (label)
SELECT 'Stage de recherche'
WHERE NOT EXISTS (SELECT 1 FROM contract_type WHERE label = 'Stage de recherche');

INSERT INTO contract_type (label)
SELECT 'Stage en entreprise'
WHERE NOT EXISTS (SELECT 1 FROM contract_type WHERE label = 'Stage en entreprise');

INSERT INTO contract_type (label)
SELECT 'Sans contrat'
WHERE NOT EXISTS (SELECT 1 FROM contract_type WHERE label = 'Sans contrat');
