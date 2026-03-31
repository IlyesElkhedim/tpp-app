export const Contracts = {
  AUCUN: "Aucun",
  ALTERNANCE_APPRENTISSAGE: "Alternance-Apprentissage",
  ALTERNANCE_PROFESSIONNALISANT: "Alternance-Professionalisant",
  STAGE_PRO: "Stage pro",
  STAGE_RECHERCHE: "Stage recherche",
  STAGE_ALTERNANCE_ETRANGER: "Stage-alternance-étranger",
} as const;

export type ContractType = typeof Contracts[keyof typeof Contracts];